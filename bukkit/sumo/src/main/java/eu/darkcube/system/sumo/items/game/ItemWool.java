/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.items.game;

import eu.darkcube.system.Plugin;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.manager.MapManager;
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

    private JavaPlugin plugin;
    private MapManager mainRuler;
    private static TeamManager teamManager;
    private Map<UUID, Map<Integer, BukkitRunnable>> playerSlotTasks = new HashMap<>();

    public ItemWool(TeamManager teamManager, Plugin plugin, MapManager mainRuler) {
        ItemWool.teamManager = teamManager;
        this.plugin = plugin;
        this.mainRuler = mainRuler;
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

        if (block.getType() == Material.WOOL) {
            int slot = player.getInventory().getHeldItemSlot();
            DyeColor color = ((Wool) block.getState().getData()).getColor();
            String worldName = player.getWorld().getName();

            playerSlotTasks.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
            Map<Integer, BukkitRunnable> slotTasks = playerSlotTasks.get(player.getUniqueId());
            if (slotTasks.containsKey(slot)) {
                slotTasks.get(slot).cancel();
            }

            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    regenerateWool(player, slot, color, worldName);
                    slotTasks.remove(slot);
                }
            };

            slotTasks.put(slot, task);
            task.runTaskLater(this.plugin, 20 * 10);
        }
    }

    private void regenerateWool(Player player, int slot, DyeColor color, String placedWorldName) {
        if (player.getWorld().equals(mainRuler.getActiveWorld()) && player.getWorld().getName().equals(placedWorldName)) {
            PlayerInventory inventory = player.getInventory();

            if (inventory.getItem(slot) == null || inventory.getItem(slot).getType() == Material.AIR) {
                ItemStack wool = new Wool(color).toItemStack(10);
                inventory.setItem(slot, wool);
            }
        }
    }

}