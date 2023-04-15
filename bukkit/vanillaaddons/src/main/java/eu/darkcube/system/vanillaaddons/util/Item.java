/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.util;

import eu.darkcube.system.inventoryapi.item.EquipmentSlot;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.item.attribute.Attribute;
import eu.darkcube.system.inventoryapi.item.attribute.AttributeModifier;
import eu.darkcube.system.inventoryapi.item.attribute.Operation;
import eu.darkcube.system.inventoryapi.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public enum Item {

	TELEPORTER(ItemBuilder.item(Material.RESPAWN_ANCHOR)
			.displayname(Component.text("Teleporter").color(NamedTextColor.GOLD))),
	FLIGHT_CHESTPLATE(ItemBuilder.item(Material.LEATHER_CHESTPLATE)
			.meta(new LeatherArmorBuilderMeta(Color.fromRGB(100, 100, 100)))
			.flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE)
			.displayname(Component.text("Flug Brustplatte", NamedTextColor.DARK_PURPLE))
			.unbreakable(true)
			.lore(Component.text("Kreativ-Flug").color(NamedTextColor.LIGHT_PURPLE))
			.attributeModifier(Attribute.GENERIC_ARMOR,
					new AttributeModifier(UUID.randomUUID(), "flight_chestplate", 0,
							Operation.ADD_NUMBER, EquipmentSlot.CHEST))),

	;

	private final ItemBuilder builder;

	Item(ItemBuilder builder) {
		this.builder = builder;
	}

	static {
		for (Item item : values()) {
			item.builder.persistentDataStorage().set(Data.TYPE_KEY, Data.TYPE, item);
		}
	}

	public ItemStack item() {
		return builder.build();
	}

	public static class Data {
		public static final Key TYPE_KEY = new Key("VanillaAddons", "itemType");
		public static final PersistentDataType<Item> TYPE =
				PersistentDataTypes.enumType(Item.class);
	}
}
