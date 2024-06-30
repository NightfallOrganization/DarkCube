/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class KillMobsCommand implements CommandExecutor {

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
			if (player.hasPermission("killmobs.use")) {
                for (Entity e : player.getWorld().getEntities()) {
                    if (!(e instanceof Player)) {
                        e.remove();
                    }
                }
                player.sendMessage("§7Alle Mobs wurden §eentfernt§7!");
            } else {
                player.sendMessage("§cDu hast keine Berechtigung, diesen Befehl auszuführen!");
            }
        }
        return true;
    }
}
