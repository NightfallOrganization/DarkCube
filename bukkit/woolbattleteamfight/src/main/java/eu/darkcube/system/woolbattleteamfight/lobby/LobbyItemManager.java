/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.lobby;

import eu.darkcube.system.woolbattleteamfight.guis.TeamGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
            // Dies verhindert, dass der Spieler irgendetwas in seinem Inventar bewegt
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = event.getPlayer().getItemInHand();
                if (item != null && item.getType() == Material.BOOK && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta.getDisplayName().equals(ChatColor.BLUE + "Teams")) {
                        TeamGUI.openTeamsInventory(event.getPlayer());
                    }
                }
            }
        }
    }

    private void setLobbyItems(Player player) {
        player.getInventory().clear();

        // Slot 1
        player.getInventory().setItem(0, createItem(Material.BOW, 1, ChatColor.GREEN + "Perks",
                new String[]{ChatColor.GRAY + "Wähle Perks, um deine Gegner zu besiegen!"},
                null, null));

        // Slot 2
        player.getInventory().setItem(1, createItem(Material.BOOK, 1, ChatColor.BLUE + "Teams",
                new String[]{
                        ChatColor.GRAY + "Wähle dein Team!",
                        ChatColor.GRAY + "Möge das beste Team gewinnen!"
                },
                null, null));

        // Slot 5
        player.getInventory().setItem(4, createItem(Material.BLAZE_ROD, 1, ChatColor.GOLD + "Partikel " + ChatColor.GREEN + "An " + ChatColor.DARK_GRAY + "╏ " + ChatColor.GRAY + "Ausschalten?",
                new String[]{ChatColor.GRAY + "Klicke um Partikel zu deaktivieren!"},
                null, null));

        // Slot 8
        player.getInventory().setItem(7, createItem(Material.REDSTONE_COMPARATOR, 1, ChatColor.RED + "Einstellungen",
                new String[]{ChatColor.GRAY + "Hier siehst du einige Einstellungen"},
                null, null));

        // Slot 9
        player.getInventory().setItem(8, createItem(Material.PAPER, 1, ChatColor.GOLD + "Voting",
                new String[]{ChatColor.GRAY + "Stimme für Spielszenarien ab!"},
                null, null));
    }


    private ItemStack createItem(Material material, int amount, String displayName, String[] lore, Enchantment[] enchantments, int[] levels) {
        return createItem(material, amount, displayName, lore, enchantments, levels, false);
    }

    private ItemStack createItem(Material material, int amount, String displayName, String[] lore, Enchantment[] enchantments, int[] levels, boolean hideEnchants) {
        short data = 0;

        if (material == Material.STAINED_GLASS) {
            data = 14;  // Für rotes Glas
        }

        ItemStack item = new ItemStack(material, amount, data);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        List<String> loreList = new ArrayList<>();
        for (String s : lore) {
            loreList.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        meta.setLore(loreList);

        if (enchantments != null) {
            for (int i = 0; i < enchantments.length; i++) {
                meta.addEnchant(enchantments[i], levels[i], true);
            }
        }

        if (hideEnchants) {
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        // Setzt den Bogen und die Schere als unzerstörbar und versteckt dieses Merkmal
        if (material == Material.BOW || material == Material.SHEARS) {
            meta.spigot().setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }

        item.setItemMeta(meta);
        return item;
    }

}
