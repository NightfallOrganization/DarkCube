/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.manager.LifeManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShowTeamLivesCommand implements CommandExecutor {

    private final LifeManager lifeManager;

    public ShowTeamLivesCommand(LifeManager lifeManager) {
        this.lifeManager = lifeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "§7Usage: /showteamlifes <Team>");
            return false;
        }

        ChatColor teamColor;

        try {
            teamColor = ChatColor.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "§7Unbekanntes Team: " + args[0]);
            return false;
        }

        int lives = lifeManager.getTeamLives(teamColor);
        sender.sendMessage(ChatColor.GREEN + "§7Das Team hat §b" + lives + " §7Leben");
        return true;
    }

}
