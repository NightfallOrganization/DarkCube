/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.items;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.PersistentDataValue;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class CustomItem {
    private final ItemStack itemStack;
    PersistentDataValue<String> name;
    PersistentDataValue<Integer> rarity;
    Plugin woolMania = WoolMania.getInstance();

    public CustomItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        name = new PersistentDataValue<>(new NamespacedKey(woolMania, "name"), String.class, itemStack.getItemMeta().getPersistentDataContainer(), "§7CustomItem");
        rarity = new PersistentDataValue<>(new NamespacedKey(woolMania, "rarity"), Integer.class, itemStack.getItemMeta().getPersistentDataContainer(), 1);
    }



}
