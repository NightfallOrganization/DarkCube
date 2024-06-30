/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;
import building.oneblock.manager.WorldSlotManager;
import building.oneblock.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.io.File;

public class DeleteWorldCommand implements CommandExecutor {
    private WorldSlotManager worldSlotManager;

    public DeleteWorldCommand (WorldSlotManager worldSlotManager) {
        this.worldSlotManager = worldSlotManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        User user = UserAPI.instance().user(player.getUniqueId());


        if (args.length != 1) {
            player.sendMessage("§cUsage: /deleteworld [world]");
            return true;
        }

        String worldName = args[0];
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            user.sendMessage(Message.ONEBLOCK_WORLD_NOT_FOUND);
            return true;
        }

        if (!Bukkit.unloadWorld(world, true)) {
            user.sendMessage(Message.ONEBLOCK_WORLD_COULD_NOT_LOAD);
            return true;
        }

        File worldFolder = world.getWorldFolder();
        deleteWorldFolder(worldFolder);
        worldSlotManager.validateWorlds(player);

        user.sendMessage(Message.ONEBLOCK_WORLD_SUCCESSFUL_DELETED, worldName);
        return true;
    }

    private void deleteWorldFolder(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteWorldFolder(file);
                    } else {
                        file.delete();
                    }
                }
            }
            path.delete();
        }
    }
}
