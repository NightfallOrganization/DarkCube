/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.game.GameScoreboard;
import eu.darkcube.system.sumo.game.items.LifeManager;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class SetLifesCommand implements CommandExecutor {

    private final TeamManager teamManager;
    private final LifeManager lifeManager;
    private final GameScoreboard gameScoreboard;

    public SetLifesCommand(TeamManager teamManager, LifeManager lifeManager, GameScoreboard gameScoreboard) {
        this.teamManager = teamManager;
        this.lifeManager = lifeManager;
        this.gameScoreboard = gameScoreboard;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cFalsche Anzahl von Argumenten. Verwendung: /setlifes (Team) (Leben)");
            return true;
        }

        String teamName = args[0];
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cBitte geben Sie eine gültige Anzahl von Leben ein");
            return true;
        }

        if (!teamManager.teamExists(teamName)) {
            sender.sendMessage("§cDas Team " + teamName + " existiert nicht!");
            return true;
        }

        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);
        if (team == null) {
            sender.sendMessage("§cInterner Fehler: Das Team konnte nicht gefunden werden!");
            return true;
        }

        for (String entry : team.getEntries()) {
            Player player = Bukkit.getPlayer(entry);
            if (player != null) {
                lifeManager.setLives(team.getName(), amount);
                gameScoreboard.updateTeamLives(teamManager, lifeManager);
            }
        }

        sender.sendMessage("§7Die Anzahl der Leben für das Team §f" + teamName + "§7 wurde auf §f" + amount + " §7gesetzt.");

        return true;
    }
}
