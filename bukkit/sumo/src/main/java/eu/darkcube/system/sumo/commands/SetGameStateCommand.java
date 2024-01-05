/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.other.GameStates;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;

public class SetGameStateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("setgamestate")) {
            if (args.length == 1) {
                try {
                    GameStates newState = GameStates.valueOf(args[0].toUpperCase());
                    GameStates.setState(newState);
                    sender.sendMessage(ChatColor.GREEN + "§7GameState wurde zu §b" + newState + " §7gesetzt.");
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "§7States: §bSTARTING§7, §bPLAYING§7, §bENDING");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "§7Usage: /setgamestate <GameState>");
                return true;
            }
        }
        return false;
    }
}
