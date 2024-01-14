/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import eu.darkcube.system.sumo.Sumo;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class WoolDespawner implements Listener {

    private final Sumo plugin;

    public WoolDespawner(Sumo plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWoolPlace(BlockPlaceEvent event) {
        if(event.getBlock().getType() == Material.WOOL) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Setze den Block auf graue Wolle
                    event.getBlock().setData((byte) 8);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            event.getBlock().setType(Material.AIR);
                        }
                    }.runTaskLater(plugin, 20L);  // 2 Sekunden (40 Ticks) nachdem es grau wurde

                }
            }.runTaskLater(plugin, 20L * 4);  // 3 Sekunden (60 Ticks) nachdem es platziert wurde
        }
    }

}
