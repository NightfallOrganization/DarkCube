/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataSet;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BasicPersistentDataStorage implements PersistentDataStorage {

	private final User user;
	private final JsonDocument data = new JsonDocument();
	private final Map<Key, Object> caches = new HashMap<>();

	public BasicPersistentDataStorage(User user) {
		this.user = user;
	}

	@Override
	public <T> void set(Key key, PersistentDataType<T> type, T data) {
		JsonDocument d = new JsonDocument();
		type.serialize(d, key.toString(), data);
		user.lock();
		this.data.append(d);
		caches.put(key, data);
		user.unlock();
		new PacketUserPersistentDataSet(user.getUniqueId(), d).sendAsync();
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
		user.lock();
		if (!this.data.contains(key.toString())) {
			set(key, type, data);
		}
		user.unlock();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		user.lock();
		boolean contains = data.contains(key.toString());
		T t = (T) caches.remove(key);
		if (t == null) {
			t = type.deserialize(this.data, key.toString());
		}
		data.remove(key.toString());
		user.unlock();
		if (!contains)
			return null;
		new PacketUserPersistentDataRemove(user.getUniqueId(), key).sendAsync();
		return t;
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
		user.lock();
		T t = get(key, type);
		if (t == null) {
			t = defaultValue.get();
			set(key, type, t);
		}
		user.unlock();
		return t;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		user.lock();
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
			user.unlock();
		}
	}

	@Override
	public boolean has(Key key) {
		user.lock();
		boolean con = data.contains(key.toString());
		user.unlock();
		return con;
	}

	public JsonDocument getData() {
		user.lock();
		JsonDocument data = this.data;
		user.unlock();
		return data;
	}

	public void clear() {
		user.lock();
		data.clear();
		caches.clear();
		user.unlock();
	}
}
