/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.inventory;

import eu.darkcube.system.inventoryapi.v1.InventoryType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class InventoryRegistry {

	private final Map<InventoryType, Supplier<Inventory<?>>> registry = new HashMap<>();

	public InventoryRegistry() {
		register(TeleporterInventory.TYPE, TeleporterInventory::new);
		register(TeleportersInventory.TYPE, TeleportersInventory::new);
		register(TeleporterRenameInventory.TYPE, TeleporterRenameInventory::new);
	}

	public void register(InventoryType type, Supplier<Inventory<?>> supplier) {
		registry.put(type, supplier);
	}

	public <Data> Inventory<Data> newInventory(InventoryType type, Data data) {
		@SuppressWarnings("unchecked")
		Inventory<Data> inv = (Inventory<Data>) registry.get(type).get();
		inv.init(data);
		return inv;
	}
}
