/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.DyeColor;
import org.bukkit.inventory.meta.ItemMeta;

public class WoolItem {

    public static ItemStack getItem(DyeColor color) {
        ItemStack wool = new ItemStack(Material.WOOL, 7, color.getWoolData());
        ItemMeta meta = wool.getItemMeta();
        meta.setDisplayName("§7Wool");
        wool.setItemMeta(meta);
        return wool;
    }
}
