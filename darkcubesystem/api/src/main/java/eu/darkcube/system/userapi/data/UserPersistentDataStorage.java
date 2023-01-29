/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.userapi.BukkitUser;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataSet;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UserPersistentDataStorage implements PersistentDataStorage {

	private final BukkitUser user;
	private final Map<Key, Object> caches = new HashMap<>();
	private JsonDocument data = new JsonDocument();

	public UserPersistentDataStorage(BukkitUser user) {
		this.user = user;
	}

	@Override
	public <T> void set(Key key, PersistentDataType<T> type, T data) {
		JsonDocument d = new JsonDocument();
		type.serialize(d, key.toString(), data);
		try {
			user.lock();
			this.data.append(d);
			caches.put(key, data);
		} finally {
			user.unlock();
		}
		new PacketUserPersistentDataSet(user.getUniqueId(), d).sendAsync();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		try {
			user.lock();
			boolean contains = data.contains(key.toString());
			T t = (T) caches.remove(key);
			if (t == null) {
				t = type.deserialize(this.data, key.toString());
			}
			data.remove(key.toString());
			if (!contains)
				return null;
			new PacketUserPersistentDataRemove(user.getUniqueId(), key).sendAsync();
			return t;
		} finally {
			user.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		try {
			user.lock();
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
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
		try {
			user.lock();
			T t = get(key, type);
			if (t == null) {
				t = defaultValue.get();
				set(key, type, t);
			}
			return t;
		} finally {
			user.unlock();
		}
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
		user.lock();
		if (!this.data.contains(key.toString())) {
			set(key, type, data);
		}
		user.unlock();
	}

	@Override
	public boolean has(Key key) {
		try {
			user.lock();
			return data.contains(key.toString());
		} finally {
			user.unlock();
		}
	}

	public void set(JsonDocument other) {
		try {
			user.lock();
			this.data = new JsonDocument();
			this.caches.clear();
			this.data.append(other);
		} finally {
			user.unlock();
		}
	}

	public void remove(Key key) {
		try {
			user.lock();
			data.remove(key.toString());
			caches.remove(key);
		} finally {
			user.unlock();
		}
	}
}
