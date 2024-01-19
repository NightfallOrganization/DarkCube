/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.manager;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.executions.Ending;
import eu.darkcube.system.sumo.scoreboards.GameScoreboard;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LifeManager {
    private final Map<ChatColor, Integer> teamLives;
    private final TeamManager teamManager;
    private GameScoreboard gameScoreboard;

    public LifeManager(TeamManager teamManager, GameScoreboard gameScoreboard) {
        this.teamManager = teamManager;
        this.gameScoreboard = gameScoreboard;
        teamLives = new HashMap<>();
        initializeTeamLives();
    }

    private void initializeTeamLives() {
        teamLives.put(TeamManager.TEAM_WHITE, 10);
        teamLives.put(TeamManager.TEAM_BLACK, 10);
    }

    public void reduceLife(UUID playerID) {
        ChatColor teamColor = teamManager.getPlayerTeam(playerID);
        if (teamColor != null && teamLives.containsKey(teamColor)) {
            int currentLives = teamLives.get(teamColor);

            if (currentLives <= 0) {
                Ending ending = new Ending(Sumo.getInstance());
                ending.execute(teamColor);
                return;
            }

            int newLives = currentLives - 1;
            teamLives.put(teamColor, newLives);

            if (teamColor == TeamManager.TEAM_BLACK) {
                gameScoreboard.updateBlackLives(newLives);
            } else if (teamColor == TeamManager.TEAM_WHITE) {
                gameScoreboard.updateWhiteLives(newLives);
            }

        }
    }

    public int getTeamLives(ChatColor teamColor) {
        return teamLives.getOrDefault(teamColor, 0);
    }

    public void setTeamLives(ChatColor teamColor, int lives) {
        teamLives.put(teamColor, lives);
    }

}