/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class Locations extends Parser {

    public static final float F360 = 360f;
    public static final float F180 = 180f;

    public static Set<Block> getBlocksInLine(Location start, Vector direction, int range) {
        BlockIterator it = new BlockIterator(start.getWorld(), start.toVector(), direction, 1, range);
        Block last;
        Set<Block> b = new HashSet<>();
        while (it.hasNext()) {
            last = it.next();
            if (last.getType() != Material.AIR) {
                b.add(last);
            }
        }
        return b;
    }

    public static Set<Block> getBlocksInLine(Player start, int range) {
        return getBlocksInLine(start, start.getLocation().getDirection(), range);
    }

    public static Set<Block> getBlocksInLine(Player start, Vector direction, int range) {
        return getBlocksInLine(start.getEyeLocation(), direction, range);
    }

    public static Location getNiceLocation(Location loc) {
        Location l = loc.clone();
        l.setYaw(getNiceYaw(l.getYaw()));
        l.setPitch(getNicePitch(l.getPitch()));
        l.setX(niceCoordinate(l.getX()));
        l.setY(niceCoordinate(l.getY()));
        l.setZ(niceCoordinate(l.getZ()));
        return l;
    }

    public static float getNicePitch(float y) {
        y += 90f;
        float interval = 22.5f;
        if (Math.round(y % interval) == y % interval) return y - 90f;

        float hInterval = interval / 2f;

        for (float i = F180; i >= 0f; i -= interval) {
            float val1 = i - hInterval;
            float val2 = i + hInterval;

            if (y >= val1 && y <= val2) {
                y = i;
                y %= F180;
                break;
            }
        }
        return y - 90f;
    }

    public static float getNiceYaw(float y) {
        float interval = 22.5f;
        float hInterval = interval / 2f;

        y = antiNegYaw(y, hInterval);

        for (float i = F360; i >= 0f; i -= interval) {

            float val1 = i - hInterval;
            float val2 = i + hInterval;

            if (y >= val1 && y < val2) {
                y = i;
                y %= F360;
                break;
            }
        }
        return y;
    }

    private static float antiNegYaw(float x, float hInterval) {
        if (x < (0f - hInterval)) {
            x = F360 + x;
        }
        if (x >= F360) {
            x %= F360;
            x = -x;
        }
        return x;
    }

    public static Location deserialize(String location, Location oldLoc) {
        try {
            if (location == null) {
                return oldLoc;
            }
            String[] locs = location.split(":");
            String X = locs[0];
            String Y = locs[1];
            String Z = locs[2];
            String Yaw = locs[3];
            String Pitch = locs[4];
            String World = locs[5];
            String IgnoreDirection = locs[6];
            double x = parseDouble(X);
            double y = parseDouble(Y);
            double z = parseDouble(Z);
            float yaw = parseFloat(Yaw);
            float pitch = parseFloat(Pitch);
            World world = parseWorld(World);
            boolean ignoreDirection = parseBoolean(IgnoreDirection);
            Location loc = new Location(world, x, y, z, yaw, pitch);
            if (oldLoc != null && ignoreDirection) loc.setDirection(oldLoc.getDirection());
            return loc;
        } catch (Throwable ignored) {
        }
        return oldLoc;
    }

    public static String serialize(Location loc, boolean ignoreDirection) {
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        String worldName = (loc.getWorld() != null ? loc.getWorld().getName() : "null");
        return x + ":" + y + ":" + z + ":" + yaw + ":" + pitch + ":" + worldName + ":" + ignoreDirection;
    }

    public static String toDisplay(Location loc) {
        return loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ();
    }

    public static String serialize(Location loc) {
        return serialize(loc, false);
    }

    private static double niceCoordinate(double coordinate) {
        return ((int) (coordinate * 2)) / 2D;
    }

}
