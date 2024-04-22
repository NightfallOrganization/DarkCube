/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.ruler;

import eu.darkcube.system.aetheria.manager.WorldManager;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;


public class MonsterWorldRuler implements Listener {

    private JavaPlugin plugin;

    public MonsterWorldRuler(JavaPlugin plugin) {
        this.plugin = plugin;
        maintainNightCycle();
    }

    private void maintainNightCycle() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (WorldManager.MONSTERWORLD != null) {
                    WorldManager.MONSTERWORLD.setTime(18000);
                    for (Player player : WorldManager.MONSTERWORLD.getPlayers()) {
                        player.setPlayerTime(6000, false);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 100L); // Wiederhole alle 5 Sekunden
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().equals(WorldManager.MONSTERWORLD)) {
            event.getPlayer().setPlayerTime(6000, false);
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (event.getFrom().equals(WorldManager.MONSTERWORLD)) {
            event.getPlayer().resetPlayerTime();
        }
        if (event.getPlayer().getWorld().equals(WorldManager.MONSTERWORLD)) {
            event.getPlayer().setPlayerTime(6000, false);
        }
    }


    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().equals(WorldManager.MONSTERWORLD) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().equals(WorldManager.MONSTERWORLD) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getWorld().equals(WorldManager.MONSTERWORLD)) {
            event.getDrops().clear();
        }
    }

}

