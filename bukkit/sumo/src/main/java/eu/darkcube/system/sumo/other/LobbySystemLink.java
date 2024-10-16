/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.system.server.cloudnet.DarkCubeServerCloudNet;
import eu.darkcube.system.sumo.manager.MapManager;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.util.GameState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LobbySystemLink {
    private MapManager mapManager;
    private TeamManager teamManager;

    public LobbySystemLink(MapManager mapManager, TeamManager teamManager) {
        this.mapManager = mapManager;
        this.teamManager = teamManager;
    }

    public void updateLobbyLink() {
        int playingPlayers = 0;
        if (GameStates.isState(GameStates.PLAYING)) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (teamManager.getPlayerTeam(player.getUniqueId()) != TeamManager.TEAM_NONE) {
                    playingPlayers++;
                }
            }
        } else {
            playingPlayers = Bukkit.getOnlinePlayers().size();
        }

        World mapname = mapManager.getActiveWorld();

        DarkCubeServerCloudNet.displayName("§d" + mapname.getName() + " §7(2x2)");
        DarkCubeServerCloudNet.playingPlayers().set(playingPlayers);
        DarkCubeServerCloudNet.maxPlayingPlayers().set(4);

        if (GameStates.isState(GameStates.STARTING)) {
            DarkCubeServerCloudNet.gameState(GameState.LOBBY);

            if (!Bukkit.getOnlinePlayers().isEmpty()) {
                DarkCubeServerCloudNet.extra(doc -> doc.append("configured", true));
            } else {
                DarkCubeServerCloudNet.extra(doc -> doc.remove("configured"));
            }

        }

        if (GameStates.isState(GameStates.PLAYING)) {
            DarkCubeServerCloudNet.gameState(GameState.INGAME);
            DarkCubeServerCloudNet.extra(doc -> doc.append("configured", true));
        }

        if (GameStates.isState(GameStates.ENDING)) {
            DarkCubeServerCloudNet.gameState(GameState.STOPPING);
        }

        InjectionLayer.boot().instance(ServiceInfoHolder.class).publishServiceInfoUpdate();

        // GameState, Playing Players, Max Players
    }

}
