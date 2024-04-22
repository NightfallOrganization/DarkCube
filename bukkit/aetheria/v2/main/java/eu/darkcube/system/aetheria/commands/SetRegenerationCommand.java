/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.player.RegenerationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetRegenerationCommand implements CommandExecutor {

    private final RegenerationManager regenerationManager;

    public SetRegenerationCommand(RegenerationManager regenerationManager) {
        this.regenerationManager = regenerationManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§cUsage: /setregeneration [player] [rate]");
            return true;
        }

        Player player = sender.getServer().getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("§cSpieler nicht gefunden.");
            return true;
        }

        try {
            double rate = Double.parseDouble(args[1]);
            regenerationManager.setRegenerationRate(player, rate);
            sender.sendMessage("§7Die Regenerationsrate wurde für §a" + player.getName() + " §7auf §a" + rate + " §7gesetzt");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid regeneration rate value.");
        }

        return true;
    }
}
