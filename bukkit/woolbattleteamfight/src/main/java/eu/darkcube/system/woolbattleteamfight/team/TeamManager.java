/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.team;

import eu.darkcube.system.woolbattleteamfight.game.ArmorManager;
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

    private MapTeamSpawns mapTeamSpawns;

    public TeamManager(MapTeamSpawns mapTeamSpawns) {
        this.mapTeamSpawns = mapTeamSpawns;

        Scoreboard mainScoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

        Team red = mainScoreboard.getTeam("Red") == null ? mainScoreboard.registerNewTeam("Red") : mainScoreboard.getTeam("Red");
        Team blue = mainScoreboard.getTeam("Blue") == null ? mainScoreboard.registerNewTeam("Blue") : mainScoreboard.getTeam("Blue");
        Team violet = mainScoreboard.getTeam("Violet") == null ? mainScoreboard.registerNewTeam("Violet") : mainScoreboard.getTeam("Violet");
        Team green = mainScoreboard.getTeam("Green") == null ? mainScoreboard.registerNewTeam("Green") : mainScoreboard.getTeam("Green");

        teams.add(red);
        teams.add(blue);
        teams.add(violet);
        teams.add(green);

        red.setPrefix("§c"); // Rot
        blue.setPrefix("§9"); // Blau
        violet.setPrefix("§5"); // Violett
        green.setPrefix("§a"); // Grün
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
            player.setDisplayName("§7" + player.getName() + "§f"); // Grau, wenn nicht in einem Team
            player.setPlayerListName("§7" + player.getName() + "§f");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePlayerDisplayName(player);
    }


    public boolean isTeamFull(String teamName) {
        Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);
        return team != null && team.getSize() >= 2;
    }

    public void addToTeam(Player player, String teamName, TeamManager teamManager) {
        System.out.println(player.getName() + " versucht, dem Team " + teamName + " hinzugefügt zu werden.");

        Team team = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeam(teamName);

        if (team != null) {
            if (team.getSize() < 2) {

                team.addEntry(player.getName());
                playerTeams.put(player, team);
                updatePlayerDisplayName(player);
                ArmorManager armorManager = new ArmorManager(teamManager);
                armorManager.setArmor(player);

                System.out.println(player.getName() + " wurde erfolgreich zum Team " + teamName + " hinzugefügt.");
            } else {
                System.out.println("Das Team " + teamName + " ist voll. Spieler " + player.getName() + " konnte nicht hinzugefügt werden.");
            }
        } else {
            System.out.println("Das Team " + teamName + " existiert nicht. Spieler " + player.getName() + " konnte nicht hinzugefügt werden.");
        }
    }

    public String getPlayerTeam(Player player) {
        Team team = playerTeams.get(player);
        if (team != null) {
            return team.getName();
        }
        return null;
    }

    public boolean teamExists(String teamName) {
        return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName) != null;
    }

}
