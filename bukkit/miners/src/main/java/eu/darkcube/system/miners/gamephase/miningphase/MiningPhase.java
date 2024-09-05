/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.miningphase;

import java.util.Map;

import eu.darkcube.system.miners.gamephase.GamePhase;
import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyPhase;
import org.bukkit.scoreboard.Team;

public class MiningPhase extends GamePhase {
    private final Map<String, Team> teams;

    public MiningPhase(LobbyPhase lobbyPhase) {
        teams = lobbyPhase.getTeams();
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
