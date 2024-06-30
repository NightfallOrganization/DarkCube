/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.game.items.LifeManager;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class MyLifesCommand implements CommandExecutor {

    private final LifeManager lifeManager;
    private final TeamManager teamManager;

    public MyLifesCommand(LifeManager lifeManager, TeamManager teamManager) {
        this.lifeManager = lifeManager;
        this.teamManager = teamManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        Player player = (Player) sender;
        Team team = teamManager.getTeamOfPlayer(player);

        if (team == null) {
            player.sendMessage("§cDu bist in keinem Team!");
            return true;
        }

        int lives = lifeManager.getLives(team.getName());
        player.sendMessage("§7Du hast §f" + lives + " §7Leben übrig");

        return true;
    }

}
