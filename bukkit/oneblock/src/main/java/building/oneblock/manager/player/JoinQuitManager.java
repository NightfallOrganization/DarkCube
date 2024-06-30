/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager.player;

import building.oneblock.manager.SpawnManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitManager implements Listener {
    private SpawnManager spawnManager;

    public JoinQuitManager(SpawnManager spawnManager) {
        this.spawnManager = spawnManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Location spawnLocation = spawnManager.getSpawnLocation();
        Player player = event.getPlayer();

        event.setJoinMessage("§7[§e+§7] " + event.getPlayer().getName());

        if (!player.hasPlayedBefore()) {
            player.teleport(spawnLocation);
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("§7[§e-§7] " + event.getPlayer().getName());
    }
}
