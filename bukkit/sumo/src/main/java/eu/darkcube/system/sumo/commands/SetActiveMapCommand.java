/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;

import eu.darkcube.system.sumo.executions.Respawn;
import eu.darkcube.system.sumo.manager.MapManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetActiveMapCommand implements CommandExecutor {
    private MapManager mapManager;
    private Respawn respawn;

    public SetActiveMapCommand(MapManager mapManager, Respawn respawn) {
        this.mapManager = mapManager;
        this.respawn = respawn;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§7Verwendung: /setactivemap [Welt]");
            return true;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            sender.sendMessage("§7Keine Welt gefunden.");
            return true;
        }

        mapManager.setActiveWorld(world);

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            respawn.teleportPlayerRandomly(player);
        }

        sender.sendMessage("§7Die aktive Map wurde zu §b" + world.getName() + " §7gesetzt.");
        return true;
    }

}
