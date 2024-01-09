/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.ruler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class MapRuler implements Listener {

    private MainRuler mainRuler;

    public MapRuler(MainRuler mainRuler) {
        this.mainRuler = mainRuler;
        maintainDaylight();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        World activeWorld = mainRuler.getActiveWorld();
        if (event.getBlock().getWorld().equals(activeWorld)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        World activeWorld = mainRuler.getActiveWorld();
        if (event.getWorld().equals(activeWorld)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        World activeWorld = mainRuler.getActiveWorld();
        if (event.getLocation().getWorld().equals(activeWorld) &&
                event.getEntityType().isAlive() &&
                event.getEntityType() != EntityType.PLAYER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity().getWorld().equals(mainRuler.getActiveWorld())) {
            event.setCancelled(true);
        }
    }

    private void maintainDaylight() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("Sumo"), new Runnable() {
            @Override
            public void run() {
                World activeWorld = mainRuler.getActiveWorld();
                activeWorld.setTime(6000); // Setzt die Zeit auf Tag
            }
        }, 0L, 600L); // 6000 Ticks entsprechen 5 Minuten
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        World activeWorld = mainRuler.getActiveWorld();
        Player player = (Player) event.getEntity();

        if (!player.getWorld().equals(activeWorld)) {
            return;
        }

        if (event instanceof EntityDamageByEntityEvent entityDamageByEntityEvent) {
            if (entityDamageByEntityEvent.getDamager() instanceof Player) {
                // Schaden durch Spieler zulassen
                return;
            }
        }

        event.setCancelled(true);
    }
}
