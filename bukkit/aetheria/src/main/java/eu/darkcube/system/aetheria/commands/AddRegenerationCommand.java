/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomHealthManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddRegenerationCommand implements CommandExecutor {

    private CustomHealthManager healthManager;

    public AddRegenerationCommand(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 2) {
            Player targetPlayer = sender.getServer().getPlayer(args[0]);
            if (targetPlayer != null) {
                int regenToAdd;
                try {
                    regenToAdd = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§7Ungültiger Regenerationswert: " + args[1]);
                    return true;
                }
                healthManager.addRegen(targetPlayer, regenToAdd);
                sender.sendMessage("§7Es wurden §a" + regenToAdd + " §7Regeneration zum Spieler §a" + targetPlayer.getName() + " §7hinzugefügt");
            } else {
                sender.sendMessage("Spieler nicht gefunden: " + args[0]);
            }
            return true;
        }
        return false;
    }
}
