/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.manager.WorldManager.MAINWORLD;

import eu.darkcube.system.woolmania.enums.TeleportLocations;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getWorld().equals(MAINWORLD) && player.getLocation().getY() < 110 && player.getGameMode() != GameMode.CREATIVE) {
            player.teleport(TeleportLocations.SPAWN.getLocation());
        }
    }

}
