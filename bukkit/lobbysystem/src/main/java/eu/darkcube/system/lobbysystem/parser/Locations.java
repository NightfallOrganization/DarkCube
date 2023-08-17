/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.parser;

import eu.cloudnetservice.driver.document.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Locations extends Parser {

    public static final Location DEFAULT = new Location(Bukkit.getWorlds().get(0), -927.5, 28, -755.5, -180, 0);

    public static final float F360 = 360f;

    public static Location getNiceLocation(Location loc) {
        Location l = loc.clone();
        l.setYaw(Locations.getNiceY(l.getYaw()));
        l.setPitch(Locations.getNiceY(l.getPitch()));
        l.setX(getNiceCoord(l.getX()));
        l.setY(getNiceCoord(l.getY()));
        l.setZ(getNiceCoord(l.getZ()));
        return l;
    }

    private static float getNiceY(float y) {
        float interval = 45f;
        float hInterval = interval / 2f;

        y = Locations.antiNegY(y, hInterval);

        for (float i = Locations.F360; i >= 0f; i -= interval) {

            float val1 = i - hInterval;
            float val2 = i + hInterval;

            if (y >= val1 && y < val2) {
                y = i;
                y %= 360f;
                break;
            }
        }
        return y;
    }

    private static double getNiceCoord(double coord) {
        coord *= 2;
        coord = Math.round(coord);
        return coord /= 2;
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
            double x = Parser.parseDouble(X);
            double y = Parser.parseDouble(Y);
            double z = Parser.parseDouble(Z);
            float yaw = Parser.parseFloat(Yaw);
            float pitch = Parser.parseFloat(Pitch);
            World world = Parser.parseWorld(World);
            boolean ignoreDirection = Parser.parseBoolean(IgnoreDirection);
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
        return x + ":" + y + ":" + z + ":" + yaw + ":" + pitch + ":" + loc.getWorld().getName() + ":" + ignoreDirection;
    }

    public static String toDisplay(Location loc) {
        return loc.getBlockX() + "/" + loc.getBlockY() + "/" + loc.getBlockZ();
    }

    public static String serialize(Location loc) {
        return Locations.serialize(loc, false);
    }

    private static float antiNegY(float x, float hInterval) {
        if (x < (0f - hInterval)) {
            x = Locations.F360 + x;
        }
        if (x >= Locations.F360) {
            x %= Locations.F360;
            x = -x;
        }
        return x;
    }

    public static Document toDocument(Location loc, boolean ignoreDirection) {
        return Document
                .newJsonDocument()
                .append("x", loc.getX())
                .append("y", loc.getY())
                .append("z", loc.getZ())
                .append("yaw", loc.getYaw())
                .append("pit", loc.getPitch())
                .append("wor", loc.getWorld().getName())
                .append("igd", ignoreDirection);
    }

    public static Location fromDocument(Document doc, Location oldLoc) {
        double x = doc.getDouble("x");
        double y = doc.getDouble("y");
        double z = doc.getDouble("z");
        float yaw = doc.getFloat("yaw");
        float pit = doc.getFloat("pit");
        World world = Bukkit.getWorld(doc.getString("wor"));
        boolean ignoreDirection = doc.getBoolean("igd");
        Location loc = new Location(world, x, y, z, yaw, pit);
        if (oldLoc != null && ignoreDirection) {
            loc.setDirection(oldLoc.getDirection());
        }
        return loc;
    }

}
