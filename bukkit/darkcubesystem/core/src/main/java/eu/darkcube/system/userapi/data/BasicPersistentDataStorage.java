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
		synchronized (this.data) {
			this.data.append(d);
			caches.put(key, data);
		}
		new PacketUserPersistentDataSet(user.getUniqueId(), d).sendAsync();
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
		synchronized (this.data) {
			if (!this.data.contains(key.toString())) {
				set(key, type, data);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		T t;
		boolean contains;
		synchronized (data) {
			contains = data.contains(key.toString());
			t = (T) caches.remove(key);
			if (t == null) {
				t = type.deserialize(this.data, key.toString());
			}
			data.remove(key.toString());
		}
		if (!contains)
			return null;
		new PacketUserPersistentDataRemove(user.getUniqueId(), key).sendAsync();
		return t;
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
		synchronized (data) {
			T t = get(key, type);
			if (t == null) {
				t = defaultValue.get();
				set(key, type, t);
			}
			return t;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		T t = null;
		synchronized (data) {
			t = (T) caches.get(key);
			if (t != null)
				return t;
			if (!data.contains(key.toString()))
				return null;
			t = type.deserialize(data, key.toString());
		}
		synchronized (caches) {
			caches.put(key, t);
		}
		return t;
	}

	@Override
	public boolean has(Key key) {
		synchronized (data) {
			return data.contains(key.toString());
		}
	}

	public JsonDocument getData() {
		synchronized (data) {
			return this.data;
		}
	}

	public void clear() {
		synchronized (data) {
			data.clear();
			caches.clear();
		}
	}
}
