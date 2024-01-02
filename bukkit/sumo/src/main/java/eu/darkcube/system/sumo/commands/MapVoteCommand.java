/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.commands;
import eu.darkcube.system.sumo.ruler.MainRuler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.World;

public class MapVoteCommand implements CommandExecutor {

    private MainRuler mainRuler;

    public MapVoteCommand(MainRuler mainRuler) {
        this.mainRuler = mainRuler;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage("§7Usage: /vote <mapName>");
            return true;
        }

        String mapName = args[0];
        World world = convertToProperWorldName(mapName);

        if (world != null) {
            mainRuler.setActiveWorld(world);
            sender.sendMessage("§7Map set to §b" + world.getName());
        } else {
            sender.sendMessage("§7Available Maps: §bOrigin§7, §bAtlas§7, §bDemonic");
        }

        return true;
    }

    private World convertToProperWorldName(String inputName) {
        if (inputName.equalsIgnoreCase("origin")) {
            return Bukkit.getWorld("Origin");
        } else if (inputName.equalsIgnoreCase("atlas")) {
            return Bukkit.getWorld("Atlas");
        } else if (inputName.equalsIgnoreCase("demonic")) {
            return Bukkit.getWorld("Demonic");
        } else {
            return null;
        }
    }
}