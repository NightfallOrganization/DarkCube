/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.player.CoreManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetCoreCommand implements CommandExecutor {
    private final CoreManager coreManager;

    public SetCoreCommand(CoreManager coreManager) {
        this.coreManager = coreManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /setcore [player] [value]");
            return true;
        }

        Player targetPlayer = sender.getServer().getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        int value;
        try {
            value = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid core value.");
            return true;
        }

        coreManager.setCoreValue(targetPlayer, value);
        sender.sendMessage("§7Die Core von §a" + targetPlayer.getName() + " §7wurden zu §a" + value + " §7gesetzt");
        return true;
    }
}
