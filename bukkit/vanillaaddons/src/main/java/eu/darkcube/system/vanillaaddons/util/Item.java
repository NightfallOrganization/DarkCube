/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.util;

import static eu.darkcube.system.server.item.component.ItemComponent.DYED_COLOR;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.components.DyedItemColor;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum Item {

    TELEPORTER(ItemBuilder.item(Material.RESPAWN_ANCHOR).displayname(Component.text("Teleporter").color(NamedTextColor.GOLD))),
    FLIGHT_CHESTPLATE(ItemBuilder.item(Material.LEATHER_CHESTPLATE).set(DYED_COLOR, new DyedItemColor(TextColor.color(100, 100, 100), false)).displayname(Component.text("Flug Brustplatte", NamedTextColor.DARK_PURPLE)).unbreakable(true).lore(Component.text("Kreativ-Flug").color(NamedTextColor.LIGHT_PURPLE)).attributeModifier(Attribute.GENERIC_ARMOR, Key.key("vanillaaddons", "flight_chestplate"), EquipmentSlotGroup.CHEST, 0, AttributeModifier.Operation.ADD_NUMBER).flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_UNBREAKABLE)),

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
