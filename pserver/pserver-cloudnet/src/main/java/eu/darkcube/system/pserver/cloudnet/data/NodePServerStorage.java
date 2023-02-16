/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet.data;

import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.Collection;
import java.util.function.Supplier;

public class NodePServerStorage implements PersistentDataStorage {

	@Override
	public Collection<Key> keys() {
		return null;
	}

	@Override
	public <T> void set(Key key, PersistentDataType<T> type, T data) {

	}

	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		return null;
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		return null;
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
		return null;
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {

	}

	@Override
	public boolean has(Key key) {
		return false;
	}
}
