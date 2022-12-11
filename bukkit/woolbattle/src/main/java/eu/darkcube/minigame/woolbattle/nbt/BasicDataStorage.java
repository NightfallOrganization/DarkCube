/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.nbt;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class BasicDataStorage implements DataStorage {

	private final Map<String, Object> data = new HashMap<>();

	@Override
	public void set(String key, Object value) {
		data.put(key, value);
	}

	@Override
	public boolean has(String key) {
		return data.containsKey(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T) data.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T remove(String key) {
		return (T) data.remove(key);
	}

	@Override
	public <T> T getOr(String key, T orElse) {
		return has(key) ? get(key) : orElse;
	}

	@Override
	public <T> T getOr(String key, Supplier<T> orElse) {
		return has(key) ? get(key) : orElse.get();
	}
}
