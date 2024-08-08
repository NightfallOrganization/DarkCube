/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportArea {
    private final World world;
    private final int x1, y1, z1, x2, y2, z2;

    public TeleportArea(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public void teleportPlayersTo(Location location) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();
            if (playerLocation.getWorld().equals(world)
                    && playerLocation.getBlockX() >= minX && playerLocation.getBlockX() <= maxX
                    && playerLocation.getBlockY() >= minY && playerLocation.getBlockY() <= maxY
                    && playerLocation.getBlockZ() >= minZ && playerLocation.getBlockZ() <= maxZ) {
                player.teleport(location);
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, (float) 1.0, (float) 1.0);
            }
        }
    }
}
