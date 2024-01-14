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
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ItemWool implements Listener {

    private JavaPlugin plugin;
    private static TeamManager teamManager;
    private Map<UUID, Map<Integer, BukkitRunnable>> playerSlotTasks = new HashMap<>();

    public ItemWool(TeamManager teamManager, Plugin plugin) {
        ItemWool.teamManager = teamManager;
        this.plugin = plugin;
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
        Block block = event.getBlockPlaced();

        // Überprüfen, ob der platzierte Block Wolle ist
        if (block.getType() == Material.WOOL) {
            int slot = player.getInventory().getHeldItemSlot();
            DyeColor color = ((Wool) block.getState().getData()).getColor();

            // Überprüfen und alte Aufgaben für diesen Slot abbrechen
            playerSlotTasks.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
            Map<Integer, BukkitRunnable> slotTasks = playerSlotTasks.get(player.getUniqueId());
            if (slotTasks.containsKey(slot)) {
                slotTasks.get(slot).cancel();
            }

            // Erstellen einer neuen Aufgabe für den Slot
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    regenerateWool(player, slot, color);
                    // Aufgabe aus der Map entfernen, wenn fertig
                    slotTasks.remove(slot);
                }
            };

            // Aufgabe in die Map einfügen und starten
            slotTasks.put(slot, task);
            task.runTaskLater(this.plugin, 20 * 10); // 20 Ticks * 10 Sekunden
        }
    }

    private void regenerateWool(Player player, int slot, DyeColor color) {
        PlayerInventory inventory = player.getInventory();
        // Stellen Sie sicher, dass der Slot nicht belegt ist
        if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
            ItemStack wool = new Wool(color).toItemStack(10);
            inventory.setItem(slot, wool);
        }
    }
}