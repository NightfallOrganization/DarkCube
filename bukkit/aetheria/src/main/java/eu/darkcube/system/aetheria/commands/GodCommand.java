/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GodCommand implements CommandExecutor {

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        if (!command.getName().equalsIgnoreCase("god") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §a/god (Person) §7um dich oder andere in den God zu setzten");
            return false;

        }

        if ((args.length == 1) && (Bukkit.getPlayer(args[0]) != null)) {
            Player player = Bukkit.getPlayer(args[0]);

            if (!player.isInvulnerable()) {

                player.setInvulnerable(true);
                player.setSaturation(40000);
                sender.sendMessage("§a" + player.getName() + "§7 wurde in den Godmodus gesetzt");
                return true;
            } else if (player.isInvulnerable()) {
                player.setInvulnerable(false);
                sender.sendMessage("§a" + player.getName() + "§7 wurde aus den Godmodus gesetzt");
                return true;
            }
        } else if (args.length == 0) {
            Player player = (Player) sender;

            if (!player.isInvulnerable()) {

                player.setInvulnerable(true);
                player.setSaturation(20);
                sender.sendMessage("§7Godmodus §aAN");
                return false;
            } else if (player.isInvulnerable()) {
                player.setInvulnerable(false);
                player.setSaturation(20);
                sender.sendMessage("§7Godmodus §aAUS");
                return true;
            }

        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §a/god (Person) §7um dich oder andere in den God zu setzten");
        return false;
    }

}
