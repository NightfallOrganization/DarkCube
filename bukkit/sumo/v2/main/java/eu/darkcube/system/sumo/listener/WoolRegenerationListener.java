/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.listener;

import eu.darkcube.system.Plugin;
import eu.darkcube.system.sumo.game.items.WoolItem;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;
import org.bukkit.DyeColor;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.event.block.BlockPlaceEvent;

public class WoolRegenerationListener implements Listener {
    private final Plugin plugin;
    private final HashMap<UUID, BukkitRunnable> activeTimers = new HashMap<>();

    public WoolRegenerationListener(Plugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if (block.getType() == Material.WOOL) {
            DyeColor color = ((Wool) block.getState().getData()).getColor();

            BukkitRunnable existingTimer = activeTimers.get(player.getUniqueId());
            if (existingTimer != null) {
                existingTimer.cancel();
                activeTimers.remove(player.getUniqueId());
            }

            int lastWoolSlot = -1;
            for (int i = 0; i < player.getInventory().getSize(); i++) {
                ItemStack item = player.getInventory().getItem(i);
                if (item != null && item.getType() == Material.WOOL && item.getItemMeta().getDisplayName().equals("§7Wool")) {
                    if (((Wool) item.getData()).getColor() == color) {
                        lastWoolSlot = i;
                    }
                }
            }

            final int savedLastWoolSlot = lastWoolSlot;

            BukkitRunnable timer = new BukkitRunnable() {
                @Override
                public void run() {
                    int totalWoolCount = 0;
                    for (ItemStack item : player.getInventory().getContents()) {
                        if (item != null && item.getType() == Material.WOOL && item.getItemMeta().getDisplayName().equals("§7Wool")) {
                            if (((Wool) item.getData()).getColor() == color) {
                                totalWoolCount += item.getAmount();
                            }
                        }
                    }

                    int amountToAdd = 7 - totalWoolCount;
                    if (amountToAdd > 0) {
                        ItemStack woolItem = WoolItem.getItem(color);
                        woolItem.setAmount(amountToAdd);
                        if (savedLastWoolSlot != -1 && player.getInventory().getItem(savedLastWoolSlot) == null) {
                            player.getInventory().setItem(savedLastWoolSlot, woolItem);
                        } else {
                            player.getInventory().addItem(woolItem);
                        }
                    }

                    activeTimers.remove(player.getUniqueId());
                }
            };

            activeTimers.put(player.getUniqueId(), timer);
            timer.runTaskLater(plugin, 20 * 10);
        }
    }


}

