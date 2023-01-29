/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

public class God implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("god") || (args.length > 1)) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/god (Person) §7um dich oder andere in den God zu setzten");
            return false;

        }


        if ((args.length == 1) && (Bukkit.getPlayer(args[0])!=null)) {
            Player player = Bukkit.getPlayer(args[0]);

            if (player.isInvulnerable() == false) {

                player.setInvulnerable(true);
                player.setSaturation(40000);
                sender.sendMessage("§b"+ player.getName() +"§7 wurde in den Godmodus gesetzt");
                return true;
            }
            else if (player.isInvulnerable() == true) {
                player.setInvulnerable(false);
                sender.sendMessage("§b"+ player.getName() +"§7 wurde aus den Godmodus gesetzt");
                return true;
            }
        }
        else if(args.length == 0) {
            Player player = (Player) sender;

            if (player.isInvulnerable() == false) {

                player.setInvulnerable(true);
                player.setSaturation(20);
                sender.sendMessage("§7Godmodus §bAN");
                return false;
            }
            else if (player.isInvulnerable() == true) {
                player.setInvulnerable(false);
                player.setSaturation(20);
                sender.sendMessage("§7Godmodus §bAUS");
                return true;
            }


        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/god (Person) §7um dich oder andere in den God zu setzten");
        return false;
    }


}
