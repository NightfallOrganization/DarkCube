/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BasicPersistentDataStorage implements PersistentDataStorage {

	protected final JsonDocument data = new JsonDocument();
	protected final Map<Key, Object> caches = new HashMap<>();

	@Override
	public <T> void set(Key key, PersistentDataType<T> type, T data) {
		JsonDocument d = new JsonDocument();
		type.serialize(d, key.toString(), data);
		this.data.append(d);
		caches.put(key, data);
		updated();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		boolean contains = data.contains(key.toString());
		T t = (T) caches.remove(key);
		if (t == null) {
			t = type.deserialize(this.data, key.toString());
		}
		data.remove(key.toString());
		if (!contains)
			return null;
		updated();
		return t;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		T t = (T) caches.get(key);
		if (t != null)
			return t;
		if (!data.contains(key.toString()))
			return null;
		t = type.deserialize(data, key.toString());
		caches.put(key, t);
		return t;
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
		T t = get(key, type);
		if (t == null) {
			t = defaultValue.get();
			set(key, type, t);
		}
		return t;
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
		if (!this.data.contains(key.toString())) {
			set(key, type, data);
		}
	}

	@Override
	public boolean has(Key key) {
		return data.contains(key.toString());
	}

	public void updated() {
	}

	public JsonDocument getData() {
		return this.data;
	}

	public void clearCaches() {
		caches.clear();
	}

	public void clear() {
		data.clear();
		caches.clear();
		updated();
	}
}
