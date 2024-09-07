/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.listener;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.gamephase.GamePhase;
import eu.darkcube.system.miners.gamephase.endphase.EndPhase;
import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyPhase;
import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyTimer;
import eu.darkcube.system.miners.gamephase.miningphase.MiningPhase;
import eu.darkcube.system.miners.utils.ItemUtil;
import eu.darkcube.system.miners.utils.MinersPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MinersPlayer minersPlayer = new MinersPlayer(player);
        GamePhase currentPhase = Miners.getInstance().getCurrentPhase();
        Miners.getInstance().minersPlayerMap.put(player, minersPlayer);
        Miners.getInstance().getGameScoreboard().createGameScoreboard(player);
        minersPlayer.teleportToLobby();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Miners.getInstance().getGameScoreboard().updateOnline(onlinePlayer);
        }

        if(currentPhase instanceof LobbyPhase) {
            ItemUtil.setLobbyPhaseItems(player);

            if (Bukkit.getOnlinePlayers().size() > 1 && !LobbyTimer.isTimerRunning) {
                new LobbyTimer().runTaskTimer(Miners.getInstance(), 0L, 20L);
            }

        } else if(currentPhase instanceof MiningPhase) {
            ItemUtil.setMiningPhaseItems(player);
        } else if(currentPhase instanceof EndPhase) {
            ItemUtil.setEndPhaseItems(player);
        }
    }
}
