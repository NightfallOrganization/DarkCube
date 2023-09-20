/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.ruler;

import eu.darkcube.system.woolbattleteamfight.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyRuler implements Listener {

    public LobbyRuler() {
        // Setzt die Welt "world" ständig auf Mittag
        Bukkit.getScheduler().runTaskTimer(Main.getPlugin(Main.class), () -> {
            World world = Bukkit.getWorld("world");
            if (world != null) {
                world.setTime(6000);
            }
        }, 0L, 20L * 60L);  // Wiederholt alle 60 Sekunden (20 Ticks * 60 Sekunden)
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Location lobbyLocation = new Location(event.getPlayer().getWorld(), 0.5, 122, 0.5);
        event.getPlayer().teleport(lobbyLocation);
        String playerName = event.getPlayer().getName();
        event.setJoinMessage("§7" + playerName + " hat das Spiel betreten");
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Überprüft, ob das Entity ein Pfeil ist
        if (event.getEntityType() == EntityType.ARROW) {
            return; // Wenn es ein Pfeil ist, beende die Methode frühzeitig
        }

        if (event.getEntity().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        event.setQuitMessage("§7" + playerName + " hat das Spiel verlassen");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            if (event.getPlayer().getLocation().getY() < 100) {
                Location newLocation = new Location(event.getPlayer().getWorld(), 0.5, 122, 0.5);
                event.getPlayer().teleport(newLocation);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            event.setCancelled(true);
        }
    }
}
