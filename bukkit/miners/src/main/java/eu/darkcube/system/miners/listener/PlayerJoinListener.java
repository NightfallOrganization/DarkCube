/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.utils.MinersPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MinersPlayer minersPlayer = new MinersPlayer(player);
        event.joinMessage(null);
        Miners.getInstance().minersPlayerMap.put(player, minersPlayer);
        Miners.getInstance().getGameScoreboard().createGameScoreboard(player);
        minersPlayer.teleportToLobby();
    }

}
