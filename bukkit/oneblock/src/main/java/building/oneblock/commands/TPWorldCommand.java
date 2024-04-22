/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TPWorldCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl ausführen");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args.length > 4) {
            return false;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            player.sendMessage("§cWelt nicht gefunden");
            return true;
        }

        Location location;

        if (args.length == 1) {
            location = world.getSpawnLocation();
        } else {
            try {
                double x = Double.parseDouble(args[1]);
                double y = Double.parseDouble(args[2]);
                double z = Double.parseDouble(args[3]);
                location = new Location(world, x, y, z);
            } catch (NumberFormatException e) {
                player.sendMessage("§cUngültige Koordinaten");
                return true;
            }
        }

        player.teleport(location);
        player.sendMessage("§7Teleportiert zur Welt: §e" + world.getName());
        return true;
    }

}
