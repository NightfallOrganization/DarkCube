/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.inventorys.UpgraderInventory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenInventoryCommand implements CommandExecutor {

    private UpgraderInventory upgraderInventory;

    public OpenInventoryCommand(UpgraderInventory upgraderInventory) {
        this.upgraderInventory = upgraderInventory;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden!");
            return true;
        }

        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("openinventory")) {
            if (args.length == 1 && "upgrader".equalsIgnoreCase(args[0])) {
                upgraderInventory.open(player);
                return true;
            } else {
                // Füge eine Nachricht für falsche Eingaben hinzu
                player.sendMessage("§cBenutze: /openinventory [inventar]");
                return true;
            }
        }

        return false;
    }

}
