/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.ruler;

import eu.darkcube.system.sumo.lobby.LobbyTimer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class MainRuler implements Listener {

    private LobbyTimer lobbyTimer;

    public MainRuler(LobbyTimer lobbyTimer) {
        this.lobbyTimer = lobbyTimer;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (event.toWeatherState()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (lobbyTimer.getGameState() == LobbyTimer.GameState.STARTED) {

            event.getPlayer().setGameMode(GameMode.SPECTATOR);
            String worldName = findWorldWithMostPlayers();
            event.getPlayer().teleport(new Location(Bukkit.getWorld(worldName), 0, 111, 0));
            player.getInventory().clear();
        }
    }

    private String findWorldWithMostPlayers() {
        int maxPlayers = 0;
        String worldWithMostPlayers = Bukkit.getWorlds().get(0).getName(); // Standardmäßig die erste Welt

        for (World world : Bukkit.getWorlds()) {
            int playerCount = world.getPlayers().size();
            if (playerCount > maxPlayers) {
                maxPlayers = playerCount;
                worldWithMostPlayers = world.getName();
            }
        }

        return worldWithMostPlayers;
    }

}
