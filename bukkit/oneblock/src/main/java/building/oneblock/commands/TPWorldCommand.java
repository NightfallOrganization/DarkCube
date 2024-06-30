/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
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
            Player player = (Player) sender;
            User user = UserAPI.instance().user(player.getUniqueId());
            user.sendMessage(Message.ONLY_PLAYERS_CAN_USE);
            return true;
        }

        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());

        if (args.length == 0 || args.length > 4) {
            return false;
        }

        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            user.sendMessage(Message.ONEBLOCK_WORLD_NOT_FOUND);
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
                user.sendMessage(Message.ONEBLOCK_INVALID_COORDINATES);
                return true;
            }
        }

        player.teleport(location);
        user.sendMessage(Message.COMMAND_TELEPORT_WORLD, world.getName());
        return true;
    }

}
