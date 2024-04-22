/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.inventorys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgraderInventory implements Listener {

    private Inventory inv;
    private final int ALLOWED_SLOT = 22;

    public UpgraderInventory() {
        inv = Bukkit.createInventory(null, 54, "§f\udaff\udfefḬ");

        inv.setItem(19, createCustomFireworkStar(25, "-10 Level"));
        inv.setItem(20, createCustomFireworkStar(26, "-1 Level"));
        inv.setItem(24, createCustomFireworkStar(27, "+1 Level"));
        inv.setItem(25, createCustomFireworkStar(28, "+10 Level"));
    }

    private ItemStack createCustomFireworkStar(int customModelData, String name) {
        ItemStack item = new ItemStack(Material.FIREWORK_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customModelData);
            meta.setDisplayName(ChatColor.GRAY + name); // Setzt den Namen in grau
            item.setItemMeta(meta);
        }
        return item;
    }

    public void open(Player player) {
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().equals(inv)) {
            if (event.getSlot() != ALLOWED_SLOT && event.getRawSlot() < inv.getSize()) {
                event.setCancelled(true);
            }

            // Wenn ein Item via Shift-Klick ins Inventar verschoben wird
            if (event.isShiftClick()) {
                if (event.getClickedInventory().equals(event.getWhoClicked().getInventory()) && inv.firstEmpty() != ALLOWED_SLOT) {
                    event.setCancelled(true);
                    event.getWhoClicked().sendMessage("§cDu kannst ein Item nur im Slot 23 ablegen!");
                }
            }
        }
    }

}
