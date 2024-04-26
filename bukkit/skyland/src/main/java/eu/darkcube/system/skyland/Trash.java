/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;

public class Trash implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("trash")) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §b/trash §7um den Mülleimer zu öffnen");
            return false;

        }

        if (args.length == 0) {
            Player player = (Player) sender;
            Inventory inventory = Bukkit.createInventory(null, 9*4,
            Component.text().content("§b              Mülleimer").build());
            player.openInventory(inventory);
            return true;
        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §b/trash §7um den Mülleimer zu öffnen");
        return false;
    }


}
