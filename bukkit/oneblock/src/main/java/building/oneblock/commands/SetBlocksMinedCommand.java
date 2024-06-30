/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import building.oneblock.manager.player.DataStorageManager;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class SetBlocksMinedCommand implements CommandExecutor {

    private final DataStorageManager dataStorageManager;

    public SetBlocksMinedCommand(DataStorageManager dataStorageManager) {
        this.dataStorageManager = dataStorageManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dieser Befehl kann nur von Spielern verwendet werden.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("Verwendung: /setblocksmined <weltname> <anzahl>");
            return false;
        }

        String worldName = args[0];
        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Die Anzahl muss eine gültige Zahl sein.");
            return false;
        }

        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            sender.sendMessage("Die Welt " + worldName + " wurde nicht gefunden.");
            return false;
        }

        dataStorageManager.setBlocksMined(world, amount);
        sender.sendMessage("§7Die Anzahl der abgebauten Blöcke in der Welt §e" + worldName + " §7wurde auf §e" + amount + " §7gesetzt");

        return true;
    }
}

