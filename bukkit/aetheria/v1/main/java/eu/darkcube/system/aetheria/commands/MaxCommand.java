/*
 * Copyright (c) 2023. [DarkCube]
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

public class MaxCommand implements CommandExecutor {

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }

        if (!command.getName().equalsIgnoreCase("max") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §a/max (Person) §7um dich zu maxen");
            return false;

        }

        if ((args.length == 1) && (Bukkit.getPlayer(args[0]) != null)) {
            Player player = Bukkit.getPlayer(args[0]);

            player.setHealth(20);
            player.setSaturation(200);
            player.setFoodLevel(20);
            sender.sendMessage("§a" + player.getName() + "§7 wurde gemaxed");
            return true;
        } else if (args.length == 0) {
            Player player = (Player) sender;

            player.setHealth(20);
            player.setSaturation(200);
            player.setFoodLevel(20);
            sender.sendMessage("§7Du wurdest §agemaxed");
            return true;

        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §a/max (Person) §7um dich zu maxen");
        return false;
    }

}
