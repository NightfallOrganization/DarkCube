/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.lobby;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

public class LobbyItemManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            setLobbyItems(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            setLobbyItems(event.getPlayer());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }

    private void setLobbyItems(Player player) {
        player.getInventory().clear();

        player.getInventory().setItem(0, createItem(Material.BREWING_STAND_ITEM, "§dFähigkeiten", "§7Wähle deine Macht!"));
        player.getInventory().setItem(1, createItem(Material.BOOK, "§9Teams", "§7Wähle dein Team!"));
        player.getInventory().setItem(4, createItem(Material.BLAZE_ROD, "§6Partikel §aAn §8╏ §7Ausschalten?", "§7Klicke um Partikel zu deaktivieren!"));
        player.getInventory().setItem(7, createItem(Material.REDSTONE_COMPARATOR, "§cEinstellungen", "§7Stelle alles nach belieben ein!"));
        player.getInventory().setItem(8, createItem(Material.PAPER, "§6Voting", "§7Stimme für Spielszenarien ab!"));
    }

    private ItemStack createItem(Material material, String name, String lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Collections.singletonList(lore));
        item.setItemMeta(meta);
        return item;
    }
}