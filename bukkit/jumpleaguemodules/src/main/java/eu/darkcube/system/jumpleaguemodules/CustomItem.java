/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.jumpleaguemodules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.event.Listener;

public class CustomItem implements Listener {

    public static ItemStack getRespawnItem() {
        ItemStack item = new ItemStack(Material.GOLD_PLATE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Respawn");
        item.setItemMeta(meta);
        return item;
    }

}
