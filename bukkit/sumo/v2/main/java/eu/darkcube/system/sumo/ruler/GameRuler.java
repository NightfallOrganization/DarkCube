/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.ruler;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.game.GameRespawn;
import eu.darkcube.system.sumo.game.items.LifeManager;
import eu.darkcube.system.sumo.lobby.LobbyTimer;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scoreboard.Team;

public class GameRuler implements Listener {

    private TeamManager teamManager;
    private GameRespawn gameRespawn;
    private LifeManager lifeManager;
    private LobbyTimer lobbyTimer;

    public GameRuler(TeamManager teamManager, GameRespawn gameRespawn, LifeManager lifeManager, LobbyTimer lobbyTimer) {
        this.teamManager = teamManager;
        this.gameRespawn = gameRespawn;
        this.lifeManager = lifeManager;
        this.lobbyTimer = lobbyTimer;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("world")) {
            return;
        }

        if (player.getLocation().getY() <= 70) {
            Team team = teamManager.getTeamOfPlayer(player);
            int teamLives = lifeManager.getLives(team.getName());

            if (teamLives > 0) {
                teamLives--;
                lifeManager.setLives(team.getName(), teamLives);

                String message;
                if (team.getName().equals("Black")) {
                    message = "§7Team §8Schwarz §7hat ein Leben verloren!";
                } else if (team.getName().equals("White")) {
                    message = "§7Team §fWeiß §7hat ein Leben verloren!";
                } else {
                    message = team.getName() + " §7hat ein Leben verloren!";
                }

                Bukkit.broadcastMessage(message);

                if (teamLives <= 0) {
                    lobbyTimer.setGameState(LobbyTimer.GameState.STOPPED);
                }
            } else {
                // Wenn die Leben bereits 0 sind und der Spieler unter Höhe 70 fällt
                lobbyTimer.setGameState(LobbyTimer.GameState.STOPPED);
                Bukkit.shutdown();
            }

            GameRespawn.teleportRandomlyAroundSpawn(player);
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
