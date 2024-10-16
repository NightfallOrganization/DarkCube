/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomHealthManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddHealthCommand implements CommandExecutor {

    private CustomHealthManager healthManager;

    public AddHealthCommand(CustomHealthManager healthManager) {
        this.healthManager = healthManager;
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§7Falsche Anzahl von Argumenten! Verwendung: §a/addhealth <Spielername> <Health>");
            return false;
        }

        String playerName = args[0];
        int healthToAdd;
        try {
            healthToAdd = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§7Ungültige §aHealth§7! Bitte geben Sie eine Zahl ein");
            return false;
        }

        Player targetPlayer = Bukkit.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage("§7Spieler nicht gefunden");
            return true;
        }
        healthManager.addMaxHealth(targetPlayer, healthToAdd);
        sender.sendMessage("§7Es wurden §a" + healthToAdd + " §7Max Health zum Spieler §a" + targetPlayer.getName() + " §7hinzugefügt");
        return true;
    }
}
