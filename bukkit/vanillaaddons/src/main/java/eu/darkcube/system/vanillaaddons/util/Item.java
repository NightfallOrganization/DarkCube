/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.util;

import java.util.UUID;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.util.Color;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum Item {

    TELEPORTER(ItemBuilder.item(Material.RESPAWN_ANCHOR).displayname(Component.text("Teleporter").color(NamedTextColor.GOLD))),
    FLIGHT_CHESTPLATE(ItemBuilder.item(Material.LEATHER_CHESTPLATE).meta(new LeatherArmorBuilderMeta(new Color(100, 100, 100))).flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE).displayname(Component.text("Flug Brustplatte", NamedTextColor.DARK_PURPLE)).unbreakable(true).lore(Component.text("Kreativ-Flug").color(NamedTextColor.LIGHT_PURPLE)).attributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "flight_chestplate", 0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST))),

    ;

    static {
        for (Item item : values()) {
            item.builder.persistentDataStorage().set(Data.TYPE_KEY, Data.TYPE, item);
        }
    }

    private final ItemBuilder builder;

    Item(ItemBuilder builder) {
        this.builder = builder;
    }

    public ItemStack item() {
        return builder.build();
    }

    public static class Data {
        public static final Key TYPE_KEY = Key.key("vanillaaddons", "item_type");
        public static final PersistentDataType<Item> TYPE = PersistentDataTypes.enumType(Item.class);
    }
}
