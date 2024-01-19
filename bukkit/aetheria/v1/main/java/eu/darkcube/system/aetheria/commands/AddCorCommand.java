/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CorManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddCorCommand implements CommandExecutor {

    private CorManager corManager;

    public AddCorCommand(CorManager corManager) {
        this.corManager = corManager;
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("§7Benutzung: §a/addcor <Spieler> <Betrag>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("§7Spieler nicht gefunden.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§7Ungültiger Betrag.");
            return true;
        }

        int currentCor = corManager.getCor(target);
        corManager.setCor(target, currentCor + amount);

        sender.sendMessage("§7Du hast §a" + amount + " §7Cor zu " + target.getName() + " hinzugefügt.");
        target.sendMessage("§7Dir wurden §a" + amount + " §7Cor hinzugefügt.");

        return true;
    }
}
