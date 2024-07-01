/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.items.game;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.other.GameStates;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemWool implements Listener {
    private final Map<UUID, BukkitRunnable> playerTasks = new HashMap<>();
    private final Map<UUID, Integer> playerLastWoolSlot = new HashMap<>(); // Neuer HashMap um den letzten Slot zu speichern
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

        // Speichere den Slot des platzierten Blocks
        int placedSlot = event.getPlayer().getInventory().getHeldItemSlot();
        playerLastWoolSlot.put(playerUUID, placedSlot);

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
        task.runTaskLater(Sumo.getInstance(), 200L);
    }

    private void adjustWoolInInventory(Player player) {
        ItemStack wool = createWool(player);
        UUID playerUUID = player.getUniqueId();

        int woolAmount = 0;
        int lastWoolSlot = playerLastWoolSlot.getOrDefault(playerUUID, -1); // Verwende den gespeicherten Slot

        // Wolle im Inventar zählen
        for (int i = 0; i < player.getInventory().getSize(); i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.WOOL) {
                woolAmount += item.getAmount();
            }
        }

        // Wolle im Cursor zählen
        ItemStack cursorItem = player.getItemOnCursor();
        if (cursorItem != null && cursorItem.getType() == Material.WOOL) {
            woolAmount += cursorItem.getAmount();
        }

        int difference = 10 - woolAmount;

        if (difference > 0) {
            wool.setAmount(difference);
            if (lastWoolSlot != -1) {
                ItemStack existingItem = player.getInventory().getItem(lastWoolSlot);
                if (existingItem == null || existingItem.getType() == Material.AIR) {
                    player.getInventory().setItem(lastWoolSlot, wool); // Setze die Wolle in den letzten Slot
                } else {
                    player.getInventory().addItem(wool); // Falls der Slot belegt ist, füge die Wolle hinzu
                }
            } else {
                player.getInventory().addItem(wool); // Wenn kein Slot gespeichert wurde, füge die Wolle hinzu
            }
        } else if (difference < 0) {
            wool.setAmount(-difference);
            player.getInventory().removeItem(wool);
        }
    }
}
