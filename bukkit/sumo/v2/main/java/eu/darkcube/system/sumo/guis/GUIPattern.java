/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIPattern implements Listener {

    private final Material GRAY_STAINED_GLASS_PANE = Material.STAINED_GLASS_PANE;
    private final byte GRAY_DATA = 7;  // Graue Glasscheibe
    private final byte DARK_GRAY_DATA = 15; // Dunkelgraue Glasscheibe

    public GUIPattern() {
        // Konstruktor
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Inventory inv = event.getInventory();
        if (inv == null) return;

        setPattern(inv);
    }

    public void setPattern(Inventory inv) {
        // Setze graue Glassscheibe
        for (int slot : new int[]{0, 1, 2, 3, 5, 6, 7, 8, 12, 14, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40, 42, 44}) {
            setItemIfEmpty(inv, slot, new ItemStack(GRAY_STAINED_GLASS_PANE, 1, GRAY_DATA));
        }

        // Setze dunkelgraue Glassscheibe
        for (int slot : new int[]{9, 10, 11, 13, 15, 16, 17, 19, 20, 21, 23, 24, 25, 27, 29, 31, 33, 35, 37, 39, 41, 43}) {
            setItemIfEmpty(inv, slot, new ItemStack(GRAY_STAINED_GLASS_PANE, 1, DARK_GRAY_DATA));
        }
    }

    private void setItemIfEmpty(Inventory inv, int slot, ItemStack item) {
        if (inv.getItem(slot) == null || inv.getItem(slot).getType() == Material.AIR) {
            inv.setItem(slot, item);
        }
    }
}
