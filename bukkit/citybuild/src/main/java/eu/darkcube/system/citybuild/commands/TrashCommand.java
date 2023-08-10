/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class TrashCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
            return false;
        }


        if(!command.getName().equalsIgnoreCase("trash")) {

            sender.sendMessage("§7Unbekannter Befehl. Nutze §a/trash §7um den Mülleimer zu öffnen");
            return false;

        }

        if (args.length == 0) {
            Player player = (Player) sender;
            Inventory inventory = Bukkit.createInventory(null, 9*4,
            Component.text().content("§f\uDAFF\uDFEFⲈ").build());
            player.openInventory(inventory);
            return true;
        }

        sender.sendMessage("§7Unbekannter Befehl. Nutze §a/trash §7um den Mülleimer zu öffnen");
        return false;
    }


}
