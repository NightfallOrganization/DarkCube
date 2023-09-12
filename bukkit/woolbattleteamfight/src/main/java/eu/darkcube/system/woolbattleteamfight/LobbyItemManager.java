/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class LobbyItemManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            setLobbyItems(event.getPlayer().getInventory());
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
            setLobbyItems(event.getPlayer().getInventory());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().getWorld().getName().equals("world")) {
            // Dies verhindert, dass der Spieler irgendetwas in seinem Inventar bewegt
            event.setCancelled(true);
        }
    }

    private void setLobbyItems(org.bukkit.inventory.PlayerInventory inventory) {
        // Slot 1
        inventory.setItem(0, createItem(Material.BOW, ChatColor.GREEN + "Perks",
                ChatColor.GRAY + "Wähle Perks, um deine Gegner zu besiegen!"));

        // Slot 2
        List<String> bookLore = new ArrayList<>();
        bookLore.add(ChatColor.GRAY + "Wähle dein Team!");
        bookLore.add(ChatColor.GRAY + "Möge das beste Team gewinnen!");
        inventory.setItem(1, createItem(Material.BOOK, ChatColor.BLUE + "Teams", bookLore));

        // Slot 5
        List<String> blazeLore = new ArrayList<>();
        blazeLore.add(ChatColor.GRAY + "Klicke um Partikel zu deaktivieren!");
        inventory.setItem(4, createItem(Material.BLAZE_ROD, ChatColor.GOLD + "Partikel " + ChatColor.GREEN + "An " + ChatColor.DARK_GRAY + "╏ " + ChatColor.GRAY + "Ausschalten?", blazeLore));

        // Slot 8
        inventory.setItem(7, createItem(Material.REDSTONE_COMPARATOR, ChatColor.RED + "Einstellungen",
                ChatColor.GRAY + "Hier siehst du einige Einstellungen"));

        // Slot 9
        inventory.setItem(8, createItem(Material.PAPER, ChatColor.GOLD + "Voting",
                ChatColor.GRAY + "Stimme für Spielszenarien ab!"));
    }

    private ItemStack createItem(Material material, String name, String lore) {
        List<String> loreList = new ArrayList<>();
        loreList.add(lore);
        return createItem(material, name, loreList);
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(name);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }
}
