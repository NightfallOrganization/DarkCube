/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.player.LevelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLevelCommand implements CommandExecutor {
    private final LevelManager levelManager;

    public SetLevelCommand(LevelManager levelManager) {
        this.levelManager = levelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /setlevel [player] [level]");
            return true;
        }

        Player targetPlayer = sender.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid level value.");
            return true;
        }

        levelManager.setLevel(targetPlayer, level);
        sender.sendMessage("§7Das Level von §a" + targetPlayer.getName() + " §7wurde zu §a" + level + " §7gesetzt");
        return true;
    }
}
