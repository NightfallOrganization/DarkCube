/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.inventory;

import eu.darkcube.system.vanillaaddons.AUser;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInventory<T, Data> implements Inventory<Data> {
	private final Map<AUser, T> guis = new HashMap<>();
	private Data data;

	@Override
	public void init(Data data) {
		this.data = data;
	}

	public Data data() {
		return data;
	}

	@Override
	public final void open(AUser user) {
		guis.put(user, openInventory(user));
	}

	@Override
	public final boolean isOpened(AUser user) {
		return guis.containsKey(user);
	}

	@Override
	public final void close(AUser user) {
		closeInventory(user, guis.remove(user));
	}

	protected abstract T openInventory(AUser user);

	protected abstract void closeInventory(AUser user, T inventory);
}
