/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.items.game;

import eu.darkcube.system.Plugin;
import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.manager.MapManager;
import eu.darkcube.system.sumo.other.GameStates;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemWool implements Listener {
    private final Map<UUID, BukkitRunnable> playerTasks = new HashMap<>();
    private static TeamManager teamManager;

    public ItemWool(TeamManager teamManager) {
        ItemWool.teamManager = teamManager;
    }

    public static ItemStack createWool(Player player) {
        ChatColor teamColor = teamManager.getPlayerTeam(player.getUniqueId());
        DyeColor color = (teamColor == TeamManager.TEAM_BLACK) ? DyeColor.BLACK : DyeColor.WHITE;
        Wool woolData = new Wool(color);
        ItemStack wool = woolData.toItemStack(10);
        ItemMeta meta = wool.getItemMeta();
        meta.setDisplayName("§7Wool");
        wool.setItemMeta(meta);

        return wool;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (event.getBlockPlaced().getType() != Material.WOOL) {
            return;
        }

        if (playerTasks.containsKey(playerUUID)) {
            return;
        }

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {

                if (GameStates.isState(GameStates.PLAYING)) {
                    adjustWoolInInventory(player);
                }

                playerTasks.remove(playerUUID);
            }
        };

        playerTasks.put(playerUUID, task);
        task.runTaskLater(Sumo.getInstance(), 200L); // 200 Ticks = 10 Sekunden
    }

    private void adjustWoolInInventory(Player player) {
        ItemStack wool = createWool(player);
        int woolAmount = 0;
        int firstWoolSlot = -1; // -1 bedeutet, dass noch kein Woll-Slot gefunden wurde.

        // Durchlaufen des Inventars, um die Menge der Wolle zu zählen.
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.WOOL) {
                woolAmount += item.getAmount();
                if (firstWoolSlot == -1) firstWoolSlot = i;
            }
        }

        // Unterschied zur Zielmenge berechnen.
        int difference = 10 - woolAmount;

        if (difference > 0) {
            // Fügt die Differenz hinzu, wenn weniger als 10 Blöcke vorhanden sind.
            wool.setAmount(difference);
            if (firstWoolSlot != -1) {
                // Versucht, die Wolle im ersten gefundenen Woll-Slot hinzuzufügen.
                player.getInventory().addItem(wool);
            } else {
                // Fügt die Wolle dem Inventar hinzu, wenn zuvor keine vorhanden war.
                player.getInventory().addItem(wool);
            }
        } else if (difference < 0) {
            // Entfernt die überschüssige Wolle, wenn mehr als 10 Blöcke vorhanden sind.
            wool.setAmount(-difference);
            player.getInventory().removeItem(wool);
        }
    }

}