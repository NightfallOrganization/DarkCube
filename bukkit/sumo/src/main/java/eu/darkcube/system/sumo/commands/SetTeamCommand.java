/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetTeamCommand implements CommandExecutor {

    private TeamManager teamManager;

    public SetTeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§7Verwendung: /setteam <Spieler> <Team>");
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("§7Spieler nicht gefunden!");
            return true;
        }

        String teamName = args[1];
        if (!teamManager.teamExists(teamName)) {
            sender.sendMessage("§7Dieses Team existiert nicht!");
            return true;
        }

        teamManager.addToTeam(targetPlayer, teamName, teamManager);
        sender.sendMessage("§7" + targetPlayer.getName() + " wurde zum Team " + teamName + " hinzugefügt");
        return true;
    }
}
