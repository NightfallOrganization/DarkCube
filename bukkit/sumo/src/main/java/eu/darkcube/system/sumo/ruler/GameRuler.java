/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.ruler;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.team.MapTeamSpawns;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class GameRuler implements Listener {

    private TeamManager teamManager;
    private MapTeamSpawns mapTeamSpawns;

    public GameRuler(TeamManager teamManager, MapTeamSpawns mapTeamSpawns) {
        this.teamManager = teamManager;
        this.mapTeamSpawns = mapTeamSpawns;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getLocation().getY() <= 70) {
            if (teamManager.isPlayerInTeam(player)) {
                Team team = teamManager.playerTeams.get(player);
                Location spawnLocation = mapTeamSpawns.getSpawnLocation("Origin", team.getName());

                if (spawnLocation != null) {
                    player.teleport(spawnLocation);
                } else {
                    player.teleport(new Location(player.getWorld(), 0, 101, 0));
                }
            } else {
                player.teleport(new Location(player.getWorld(), 0, 101, 0));
            }
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            World world = player.getWorld();

            if (!world.getName().equalsIgnoreCase("world")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        if (!world.getName().equalsIgnoreCase("world")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Material blockType = event.getBlock().getType();

        if (!world.getName().equalsIgnoreCase("world")) {
            if (blockType != Material.WOOL) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String fromWorld = event.getFrom().getName();
        String currentWorld = player.getWorld().getName();

        // Prüfen ob der Spieler von "world" kam und jetzt in einer anderen Welt ist.
        if (fromWorld.equalsIgnoreCase("world") && !currentWorld.equalsIgnoreCase("world")) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        // Prüfen ob der Spieler von einer anderen Welt kam und jetzt in "world" ist.
        if (!fromWorld.equalsIgnoreCase("world") && currentWorld.equalsIgnoreCase("world")) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            World world = player.getWorld();

            if (!world.getName().equalsIgnoreCase("world")) {
                if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Sumo.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            player.setHealth(player.getHealth() + event.getFinalDamage());
                        }
                    }, 1L);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

}
