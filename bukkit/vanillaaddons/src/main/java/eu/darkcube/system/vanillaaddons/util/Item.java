/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.util;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum Item {

	TELEPORTER(ItemBuilder.item(Material.RESPAWN_ANCHOR)
			.displayname(Component.text("Teleporter").color(NamedTextColor.GOLD))),

	;

	private final ItemBuilder builder;
	private boolean initialized = false;

	Item(ItemBuilder builder) {
		this.builder = builder;
	}

	public ItemStack item() {
		if (!initialized) {
			builder.persistentDataStorage().set(Data.TYPE_KEY, Data.TYPE, this);
			initialized = true;
		}
		return builder.build();
	}

	public static class Data {
		public static final Key TYPE_KEY = new Key("VanillaAddons", "itemType");
		public static final PersistentDataType<Item> TYPE =
				PersistentDataTypes.enumType(Item.class);
	}
}
