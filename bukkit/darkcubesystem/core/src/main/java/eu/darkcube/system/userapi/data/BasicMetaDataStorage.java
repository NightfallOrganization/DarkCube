/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.data;

import java.util.concurrent.ConcurrentHashMap;

public class BasicMetaDataStorage implements MetaDataStorage {

	public final ConcurrentHashMap<Key, Object> data = new ConcurrentHashMap<>();

	@Override
	public void set(Key key, Object value) {
		data.put(key, value);
	}

	@Override
	public boolean has(Key key) {
		return data.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(Key key) {
		return (T) data.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(Key key) {
		return (T) data.remove(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getOr(Key key, T orElse) {
		return (T) data.getOrDefault(key, orElse);
	}

}
