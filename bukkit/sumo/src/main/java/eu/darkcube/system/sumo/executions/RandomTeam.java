/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.executions;

import eu.darkcube.system.sumo.manager.TeamManager;
import org.bukkit.ChatColor;

import java.util.*;

public class RandomTeam {
    private TeamManager teamManager;
    private Random random;

    public RandomTeam(TeamManager teamManager) {
        this.teamManager = teamManager;
        this.random = new Random();
    }

    public void balanceTeams(Collection<UUID> playerIDs) {
        List<UUID> blackTeamPlayers = new ArrayList<>();
        List<UUID> whiteTeamPlayers = new ArrayList<>();
        List<UUID> unassignedPlayers = new ArrayList<>();

        for (UUID playerID : playerIDs) {
            ChatColor team = teamManager.getPlayerTeam(playerID);
            if (team == TeamManager.TEAM_BLACK) {
                blackTeamPlayers.add(playerID);
            } else if (team == TeamManager.TEAM_WHITE) {
                whiteTeamPlayers.add(playerID);
            } else {
                unassignedPlayers.add(playerID);
            }
        }

        while (!unassignedPlayers.isEmpty()) {
            UUID playerID = unassignedPlayers.remove(random.nextInt(unassignedPlayers.size()));
            if (blackTeamPlayers.size() <= whiteTeamPlayers.size()) {
                teamManager.setPlayerTeam(playerID, TeamManager.TEAM_BLACK);
                blackTeamPlayers.add(playerID);
            } else {
                teamManager.setPlayerTeam(playerID, TeamManager.TEAM_WHITE);
                whiteTeamPlayers.add(playerID);
            }
        }

        while (Math.abs(blackTeamPlayers.size() - whiteTeamPlayers.size()) > 1) {
            if (blackTeamPlayers.size() > whiteTeamPlayers.size()) {
                movePlayerToOtherTeam(blackTeamPlayers, whiteTeamPlayers, TeamManager.TEAM_WHITE);
            } else {
                movePlayerToOtherTeam(whiteTeamPlayers, blackTeamPlayers, TeamManager.TEAM_BLACK);
            }
        }
    }

    private void movePlayerToOtherTeam(List<UUID> fromTeam, List<UUID> toTeam, ChatColor toTeamColor) {
        UUID playerToMove = fromTeam.remove(random.nextInt(fromTeam.size()));
        teamManager.setPlayerTeam(playerToMove, toTeamColor);
        toTeam.add(playerToMove);
    }
}
