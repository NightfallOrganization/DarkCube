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
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
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

        if (event.getBlockPlaced().getType() == Material.WOOL) {
            DyeColor color = DyeColor.getByWoolData((byte) event.getBlockPlaced().getData());

            // Überprüfung, ob bereits ein Timer für diesen Spieler aktiv ist
            if (activeTimers.containsKey(player.getUniqueId())) {
                return;
            }

            int currentWoolCount = 0;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() == Material.WOOL && item.getDurability() == color.getWoolData()) {
                    currentWoolCount += item.getAmount();
                }
            }

            if (currentWoolCount < 8) {
                BukkitRunnable timer = new BukkitRunnable() {
                    @Override
                    public void run() {
                        ItemStack woolItem = WoolItem.getItem(color);
                        woolItem.setAmount(7);
                        player.getInventory().remove(Material.WOOL); // Entferne alle Woll-Items
                        player.getInventory().addItem(woolItem); // Füge 7 Woll-Items hinzu
                        activeTimers.remove(player.getUniqueId()); // Entferne den Timer aus der Liste
                    }
                };

                activeTimers.put(player.getUniqueId(), timer); // Füge den Timer zur Liste hinzu
                timer.runTaskLater(plugin, 20 * 10); // Starte den Timer
            }
        }
    }
}

