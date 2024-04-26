/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NightCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        Player player = (Player) sender;

        if((args.length == 0) && command.getName().equalsIgnoreCase("night")) {

            World world = player.getWorld();
            world.setTime(14000);
            sender.sendMessage("§7Du hast §aNacht §7gesetzt");

            return true;
        }



        sender.sendMessage("§7Unbekannter Befehl. Nutze §a/night §7um Nacht zu setzten");
        return false;
    }


}
