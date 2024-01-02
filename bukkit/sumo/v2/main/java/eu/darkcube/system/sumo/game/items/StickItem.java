/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StickItem {

    public static ItemStack getItem() {
        ItemStack stick = new ItemStack(Material.STICK);

        // Metadaten des Sticks (Name, Unzerstörbarkeit, etc.)
        ItemMeta meta = stick.getItemMeta();

        // Setze den Namen des Sticks
        meta.setDisplayName("§7Stab");

        // Mache den Stick unzerstörbar
        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

        // Füge dem Stick das Knockback 2-Verzauberung hinzu
        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);

        // Setze die Metadaten auf den Stick
        stick.setItemMeta(meta);

        return stick;
    }
}