/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.manager;

import eu.darkcube.system.sumo.other.GameStates;
import eu.darkcube.system.sumo.ruler.MainRuler;
import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import eu.darkcube.system.sumo.executions.Spectator;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MainManager implements Listener {
    private LobbyScoreboard lobbyScoreboard;
    private MainRuler mainRuler;

    public MainManager(LobbyScoreboard lobbyScoreboard, MainRuler mainRuler) {
        this.lobbyScoreboard = lobbyScoreboard;
        this.mainRuler = mainRuler;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(ChatColor.GRAY + player.getName() + " hat das Spiel betreten");

        if (GameStates.getState() == GameStates.STARTING || GameStates.getState() == GameStates.ENDING) {
            Location spawnLocation = new Location(Bukkit.getWorld("world"), 0.5, 101, 0.5);
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(spawnLocation);
        } else if (GameStates.getState() == GameStates.PLAYING) {
            Spectator.setPlayerToSpectator(player);
        }

        if (GameStates.isState(GameStates.STARTING)) {
            ItemManager.setStartingItems(player);
        } else if (GameStates.isState(GameStates.PLAYING)) {
            ItemManager.setPlayingItems(player);
        } else if (GameStates.isState(GameStates.ENDING)) {
            ItemManager.setEndingItems(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(ChatColor.GRAY + event.getPlayer().getName() + " hat das Spiel verlassen");
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();

        player.getInventory().clear();

        if (worldName.equals("world")) {
            if (GameStates.isState(GameStates.STARTING)) {
                ItemManager.setStartingItems(player);
            } else if (GameStates.isState(GameStates.ENDING)) {
                ItemManager.setEndingItems(player);
            }
        }

        if (worldName.equals(mainRuler.getActiveWorld().getName())) {
            if (GameStates.isState(GameStates.PLAYING)) {
                ItemManager.setPlayingItems(player);
            }
        }

    }

}
