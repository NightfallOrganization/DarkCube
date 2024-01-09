/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.manager.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class SetTeamCommand implements CommandExecutor {
    private TeamManager teamManager;

    public SetTeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "§7Usage: /setteam [player] [team]");
            return false;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "§7Spieler nicht gefunden");
            return false;
        }

        ChatColor teamColor;
        String teamName;
        switch (args[1].toLowerCase()) {
            case "white":
                teamColor = TeamManager.TEAM_WHITE;
                teamName = "White";
                break;
            case "black":
                teamColor = TeamManager.TEAM_BLACK;
                teamName = "Black";
                break;
            default:
                sender.sendMessage(ChatColor.RED + "§7Ungültiges Team");
                return false;
        }

        teamManager.setPlayerTeam(targetPlayer.getUniqueId(), teamColor);
        sender.sendMessage("§b" + targetPlayer.getName() + " §7wurde zu Team §b" + teamName + " §7gesetzt");
        return true;
    }
}
