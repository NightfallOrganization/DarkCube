/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.commands;

import eu.darkcube.system.woolbattleteamfight.team.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class MyTeamCommand implements CommandExecutor {

    private TeamManager teamManager;

    public MyTeamCommand(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl verwenden!");
            return true;
        }

        Player player = (Player) sender;

        if (teamManager.isPlayerInTeam(player)) {
            Team team = teamManager.playerTeams.get(player);
            player.sendMessage("§7Du bist im Team: " + team.getPrefix() + team.getName());
        } else {
            player.sendMessage("§cDu bist in keinem Team!");
        }

        return true;
    }
}
