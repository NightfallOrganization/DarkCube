/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;
import eu.darkcube.system.sumo.manager.MapManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ShowActiveMapCommand implements CommandExecutor {

    private MapManager mainRuler;

    public ShowActiveMapCommand(MapManager mainRuler) {
        this.mainRuler = mainRuler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        sender.sendMessage("§7Active Map: §b" + mainRuler.getActiveWorld());
        return true;
    }
}
