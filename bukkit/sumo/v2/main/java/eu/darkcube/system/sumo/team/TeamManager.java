/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.team;

import eu.darkcube.system.sumo.game.ArmorManager;
import eu.darkcube.system.sumo.game.items.LifeManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamManager implements Listener {

    final List<Team> teams = new ArrayList<>();
    public final Map<Player, Team> playerTeams = new HashMap<>();

    private ArmorManager armorManager;

    public TeamManager(ArmorManager armorManager) {
        this.armorManager = armorManager;

        Scoreboard mainScoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        Team black = mainScoreboard.getTeam("Black") == null ? mainScoreboard.registerNewTeam("Black") : mainScoreboard.getTeam("Black");
        Team white = mainScoreboard.getTeam("White") == null ? mainScoreboard.registerNewTeam("White") : mainScoreboard.getTeam("White");

        teams.add(black);
        teams.add(white);

        black.setPrefix("§8"); // Schwarz
        white.setPrefix("§f"); // Weiß

    }

    public int getTotalLivesOfTeam(String teamName, LifeManager lifeManager) {
        int totalLives = 0;
        Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);
        if (team != null) {
            totalLives = lifeManager.getLives(team.getName()) * team.getEntries().size();
        }
        return totalLives;
    }

    public boolean isPlayerInTeam(Player player) {
        return playerTeams.containsKey(player);
    }

    public void updatePlayerDisplayName(Player player) {
        if (isPlayerInTeam(player)) {
            Team team = playerTeams.get(player);
            player.setDisplayName(team.getPrefix() + player.getName() + "§f");
            player.setPlayerListName(team.getPrefix() + player.getName() + "§f");
        } else {
            player.setDisplayName("§7" + player.getName() + "§f");
            player.setPlayerListName("§7" + player.getName() + "§f");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerDisplayName(player);
    }

    public Team getTeamOfPlayer(Player player) {
        return playerTeams.get(player);
    }

    public void removePlayerFromTeam(Player player) {
        if (isPlayerInTeam(player)) {
            Team currentTeam = playerTeams.get(player);
            currentTeam.removeEntry(player.getName());
            playerTeams.remove(player);
            updatePlayerDisplayName(player);
            System.out.println(player.getName() + " wurde aus dem Team " + currentTeam.getName() + " entfernt.");
        }
    }

    public boolean isTeamFull(String teamName) {
        Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);
        return team != null && team.getSize() >= 2;
    }

    public void addToTeam(Player player, String teamName, TeamManager teamManager) {
        System.out.println(player.getName() + " versucht, dem Team " + teamName + " hinzugefügt zu werden.");

        Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);

        armorManager.setArmor(player, team);

        if (team != null) {
            if (team.getSize() < 2) {

                team.addEntry(player.getName());
                playerTeams.put(player, team);
                updatePlayerDisplayName(player);

                System.out.println(player.getName() + " wurde erfolgreich zum Team " + teamName + " hinzugefügt.");
            } else {
                System.out.println("Das Team " + teamName + " ist voll. Spieler " + player.getName() + " konnte nicht hinzugefügt werden.");
            }
        } else {
            System.out.println("Das Team " + teamName + " existiert nicht. Spieler " + player.getName() + " konnte nicht hinzugefügt werden.");
        }
    }

    public boolean teamExists(String teamName) {
        return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName) != null;
    }

}
