/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.lobbyphase;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.system.miners.gamephase.GamePhase;
import org.bukkit.scoreboard.Team;

public class LobbyPhase extends GamePhase {
    private final Map<String, Team> teams = new HashMap<>();
    public LobbyPhase() {
        listeners.add(new LobbyPhaseRuler());
        listeners.add(new LobbyRightClick());
    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }

    public Map<String, Team> getTeams() {
        return teams;
    }
}
