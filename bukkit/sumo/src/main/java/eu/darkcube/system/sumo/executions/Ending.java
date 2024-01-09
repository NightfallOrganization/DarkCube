/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.executions;

import eu.darkcube.system.sumo.other.GameStates;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Ending {

    public void execute(ChatColor teamColor) {
        GameStates.setState(GameStates.ENDING);

        World world = Bukkit.getWorld("world");
        if (world != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(world.getSpawnLocation());
            }
        } else {
            System.out.println("Welt 'world' nicht gefunden!");
        }
    }

}
