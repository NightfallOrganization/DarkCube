/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;

public class ListenerPlayerQuit implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (Miners.getTeamManager().getPlayerTeam(e.getPlayer()) != 0)
            Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, e.getPlayer().getName());
        e.setQuitMessage(null);
        Miners.getPlayerManager().removePlayer(e.getPlayer());

        if (Miners.getGamephase() == 0 && Miners.getLobbyPhase().getTimer().isRunning()) {
            if (Bukkit.getOnlinePlayers().size() == 2)
                Miners.getLobbyPhase().getTimer().cancel(false);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerKickEvent e) {
        if (Miners.getGamephase() != 3 && Miners.getTeamManager().getPlayerTeam(e.getPlayer()) != 0)
            Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, e.getPlayer().getName());
        e.setLeaveMessage(null);
        Miners.getPlayerManager().removePlayer(e.getPlayer());
        Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, e.getPlayer().getName());
        if (Miners.getGamephase() == 0 && Miners.getLobbyPhase().getTimer().isRunning()) {
            if (Bukkit.getOnlinePlayers().size() == 2)
                Miners.getLobbyPhase().getTimer().cancel(false);
        }
    }

}
