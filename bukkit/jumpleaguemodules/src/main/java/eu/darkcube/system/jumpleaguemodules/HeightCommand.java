/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.jumpleaguemodules;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HeightCommand implements CommandExecutor {

    private Main plugin;

    public HeightCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§7Dieser Befehl kann nur von Spielern ausgeführt werden!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§7Bitte geben Sie eine Höhe an. Benutzung: /setheight <Höhe>");
            return true;
        }

        try {
            double height = Double.parseDouble(args[0]);
            plugin.getConfig().set("respawnHeight", height);
            plugin.saveConfig();
            sender.sendMessage("§7Respawn Höhe wurde auf §6" + height + "§7 gesetzt.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§7Bitte geben Sie eine gültige Zahl für die Höhe ein");
        }
        return true;
    }
}
