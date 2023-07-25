/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Fly implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("fly") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/fly (Person) §7um dich oder andere in den Flugmodus zu setzten");
            return false;

        }


        if ((args.length == 1) && (Bukkit.getPlayer(args[0])!=null)) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player.getAllowFlight() == false) {

                player.setAllowFlight(true);
                sender.sendMessage("§b"+ player.getName() +"§7 wurde in den Flugmodus gesetzt");
                return true;
            }
            else if (player.getAllowFlight() == true) {
                player.setAllowFlight(false);
                sender.sendMessage("§b"+ player.getName() +"§7 wurde aus den Flugmodus gesetzt");
                return true;
            }
        }
        else if(args.length == 0) {
            Player player = (Player) sender;

            if (player.getAllowFlight() == false) {

                player.setAllowFlight(true);
                sender.sendMessage("§7Flugmodus §bAN");
                return false;
            }
            else if (player.getAllowFlight() == true) {
                player.setAllowFlight(false);
                sender.sendMessage("§7Flugmodus §bAUS");
                return true;
            }


        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/fly (Person) §7um dich oder andere in den Flugmodus zu setzten");
        return false;
    }


}
