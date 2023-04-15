/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

import java.awt.*;
import java.io.File;

public class CreateWorld implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("createworld")) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/createworld (world) §7um Welten zu erstellen");
            return false;

        }

        if (args.length == 1) {
            Player player = (Player) sender;
            World world = Bukkit.getWorld(args[0]);
            Server server = Bukkit.getServer();

            File f = new File("./" + args[0]);
            if(f.exists()) {
                sender.sendMessage("§7Die Welt§b " + args[0] + " §7existiert bereits");
                return true;
            }

            WorldCreator creator = new WorldCreator(args[0]);
            World meineWelt = creator.createWorld();

            sender.sendMessage("§7Du hast die Welt§b " + args[0] + " §7erstellt");
            return true;
        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/createworld (world) §7um Welten zu erstellen");
        return false;
    }


}
