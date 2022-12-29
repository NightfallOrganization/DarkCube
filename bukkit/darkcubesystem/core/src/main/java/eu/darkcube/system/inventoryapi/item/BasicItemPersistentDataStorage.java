/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.inventoryapi.item;

import eu.darkcube.system.util.data.BasicPersistentDataStorage;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;

public class BasicItemPersistentDataStorage extends BasicPersistentDataStorage
		implements ItemPersistentDataStorage {
	private final ItemBuilder builder;

	public BasicItemPersistentDataStorage(ItemBuilder builder) {
		this.builder = builder;
	}

	@Override
	public <T> ItemPersistentDataStorage iset(Key key, PersistentDataType<T> type, T data) {
		set(key, type, data);
		return this;
	}

	@Override
	public <T> ItemPersistentDataStorage isetIfNotPresent(Key key, PersistentDataType<T> type,
			T data) {
		setIfNotPresent(key, type, data);
		return this;
	}

	@Override
	public ItemBuilder builder() {
		return builder;
	}
}
