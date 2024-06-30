/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.player.XPManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetXPCommand implements CommandExecutor {
    private final XPManager xpManager;

    public SetXPCommand(XPManager xpManager) {
        this.xpManager = xpManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /setxp [player] [value]");
            return true;
        }

        Player targetPlayer = sender.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        double xp;
        try {
            xp = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid XP value.");
            return true;
        }

        xpManager.setXP(targetPlayer, xp);
        sender.sendMessage("§7Die XP von §a" + targetPlayer.getName() + " §7wurden zu §a" + xp + "XP §7gesetzt");
        return true;
    }
}
