/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.team;

import eu.darkcube.system.sumo.game.ArmorManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TeamDistributor {

    private TeamManager teamManager;
    private ArmorManager armorManager;

    public TeamDistributor(TeamManager teamManager, ArmorManager armorManager) {
        this.teamManager = teamManager;
        this.armorManager = armorManager;
    }

    public void distributeTeamsAfterTimer(TeamManager teamManager) {
        List<Team> teamsWithOnePlayer = new ArrayList<>();
        List<Team> emptyTeams = new ArrayList<>();
        List<Player> playersWithoutTeam = new ArrayList<>();

        // Kategorisieren der Teams und Spieler
        for (Team team : teamManager.teams) {
            if (team.getSize() == 1) {
                teamsWithOnePlayer.add(team);
            } else if (team.getSize() == 0) {
                emptyTeams.add(team);
            }
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            Team team = teamManager.getTeamOfPlayer(player);
            armorManager.setArmor(player, team);

            if (!teamManager.isPlayerInTeam(player)) {
                playersWithoutTeam.add(player);
            }
        }

        // Wenn es nur 2 Spieler in demselben Team gibt, setze einen in ein anderes Team
        for (Team team : teamManager.teams) {
            if (team.getSize() == 2 && Bukkit.getOnlinePlayers().size() == 2) {
                String playerNameToMove = team.getEntries().stream().findAny().get();
                Player playerToMove = Bukkit.getPlayer(playerNameToMove);
                Team currentTeam = teamManager.getTeamOfPlayer(playerToMove);
                String teamName = currentTeam.getName();
                Team randomTeam = getRandomTeamExcludingCurrent(currentTeam, teamName, playerToMove);
                if (randomTeam != null) {
                    team.removeEntry(playerToMove.getName());
                    randomTeam.addEntry(playerToMove.getName());
                    teamManager.playerTeams.put(playerToMove, randomTeam);
                    teamManager.updatePlayerDisplayName(playerToMove);
                }
                break;
            }
        }

        // Verteilen der spieler ohne Teams
        for (Player player : playersWithoutTeam) {
            Team team = teamManager.getTeamOfPlayer(player);
            armorManager.setArmor(player, team);

            if (!emptyTeams.isEmpty()) {
                Team randomEmptyTeam = emptyTeams.get(new Random().nextInt(emptyTeams.size()));
                randomEmptyTeam.addEntry(player.getName());
                teamManager.playerTeams.put(player, randomEmptyTeam);
                teamManager.updatePlayerDisplayName(player);
                emptyTeams.remove(randomEmptyTeam);
            } else if (!teamsWithOnePlayer.isEmpty()) {
                Team randomTeamWithOnePlayer = teamsWithOnePlayer.get(new Random().nextInt(teamsWithOnePlayer.size()));
                randomTeamWithOnePlayer.addEntry(player.getName());
                teamManager.playerTeams.put(player, randomTeamWithOnePlayer);
                teamManager.updatePlayerDisplayName(player);
                teamsWithOnePlayer.remove(randomTeamWithOnePlayer);
            }
        }
    }

    private Team getRandomTeamExcludingCurrent(Team currentTeam, String teamName, Player player) {
        List<Team> otherTeams = teamManager.teams.stream().filter(team -> !team.getName().equals(currentTeam.getName())).collect(Collectors.toList());

        Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);
        armorManager.setArmor(player, team);

        if (otherTeams.isEmpty()) {
            return null;
        }
        return otherTeams.get(new Random().nextInt(otherTeams.size()));
    }
}
