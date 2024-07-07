/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.util;

import building.oneblock.OneBlock;
import building.oneblock.manager.WorldManager;
import building.oneblock.manager.WorldSlotManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class StartWorld {
    private int taskID = -1;
    private WorldSlotManager worldSlotManager;

    public StartWorld (WorldSlotManager worldSlotManager) {
        this.worldSlotManager = worldSlotManager;
    }

    public void execute(Player player, int slot) {
        startWorldTask(player, slot);
    }

    private void startWorldTask(Player player, int slot) {
        startActionBarTask(player);
        World playerWorld = WorldManager.createPlayerSpecificVoidWorld(player.getName());
        // teleportIfWorldIsCreated(slot, playerWorld, player);
    }

    public void teleportIfWorldIsCreated(int slot, World playerWorld, Player player) {
        User user = UserAPI.instance().user(player.getUniqueId());

        if (playerWorld != null) {
            Location location = new Location(playerWorld, 0.5, 100, 0.5);
            //            Bukkit.getScheduler().runTaskLater(OneBlock.getInstance(), () -> {
            player.teleportAsync(location).thenAccept(success -> {
                user.sendMessage(Message.ONEBLOCK_WELCOME_WORLD);
                stopActionBarTask();
                worldSlotManager.setWorld(slot, playerWorld, player);
            });
            //            }, 100L); // 100 ticks entsprechen 5 Sekunden

        } else {
            user.sendMessage(Message.ONEBLOCK_CREATING_WORLD_ERROR);
            stopActionBarTask();
        }
    }

    public void startActionBarTask(Player player) {
        User user = UserAPI.instance().user(player.getUniqueId());

        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(OneBlock.getInstance(), new Runnable() {
            private boolean dot = false;

            @Override
            public void run() {
                if (dot) {
                    user.sendActionBar(Message.ONEBLOCK_CREATING_WORLD_1);
                } else {
                    user.sendActionBar(Message.ONEBLOCK_CREATING_WORLD_2);
                }
                dot = !dot;
            }
        }, 0L, 20L);
    }

    public void stopActionBarTask() {
        if (taskID != -1) {
            Bukkit.getScheduler().cancelTask(taskID);
            taskID = -1;
        }
    }

}
