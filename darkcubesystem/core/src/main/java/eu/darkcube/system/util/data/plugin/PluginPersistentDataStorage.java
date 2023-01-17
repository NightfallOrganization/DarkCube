/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data.plugin;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.Plugin;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class PluginPersistentDataStorage implements PersistentDataStorage {

	public final JsonDocument data = new JsonDocument();
	public final Lock lock = new ReentrantLock(false);
	public final Plugin plugin;
	private final Map<Key, Object> caches = new HashMap<>();

	public PluginPersistentDataStorage(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public <T> void set(Key key, PersistentDataType<T> type, T data) {
		JsonDocument d = new JsonDocument();
		type.serialize(d, key.toString(), data);
		lock.lock();
		this.data.append(d);
		caches.put(key, data);
		lock.unlock();
		new PacketPluginDataSet(plugin.getName(), d).sendAsync();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		lock.lock();
		boolean contains = data.contains(key.toString());
		T t = (T) caches.remove(key);
		if (t == null) {
			t = type.deserialize(this.data, key.toString());
		}
		data.remove(key.toString());
		lock.unlock();
		if (!contains)
			return null;
		new PacketPluginDataRemove(plugin.getName(), key).sendAsync();
		return t;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		lock.lock();
		try {
			T t = (T) caches.get(key);
			if (t != null)
				return t;
			if (!data.contains(key.toString()))
				return null;
			t = type.deserialize(data, key.toString());
			caches.put(key, t);
			return t;
		} finally {
			lock.unlock();
		}
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
		lock.lock();
		T t = get(key, type);
		if (t == null) {
			t = defaultValue.get();
			set(key, type, t);
		}
		lock.unlock();
		return t;
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
		lock.lock();
		if (!this.data.contains(key.toString())) {
			set(key, type, data);
		}
		lock.unlock();
	}

	@Override
	public boolean has(Key key) {
		lock.lock();
		boolean con = data.contains(key.toString());
		lock.unlock();
		return con;
	}

	public JsonDocument getData() {
		lock.lock();
		JsonDocument data = this.data;
		lock.unlock();
		return data;
	}
}

