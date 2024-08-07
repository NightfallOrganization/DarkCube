/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.parser;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Locations extends Parser {

    public static final Location DEFAULT = new Location(Bukkit.getWorlds().getFirst(), 0.5, 80, 0.5, -180, 0);
    public static final PersistentDataType<Location> TYPE = PersistentDataTypes.create(jsonElement -> fromJson(jsonElement.getAsJsonObject(), DEFAULT), location -> toJson(location, null), Location::clone);

    public static final float F360 = 360f;

    public static Location getNiceLocation(Location loc) {
        var l = loc.clone();
        l.setYaw(Locations.getNiceY(l.getYaw()));
        l.setPitch(Locations.getNiceY(l.getPitch()));
        l.setX(getNiceCoord(l.getX()));
        l.setY(getNiceCoord(l.getY()));
        l.setZ(getNiceCoord(l.getZ()));
        return l;
    }

    private static float getNiceY(float y) {
        var interval = 45f;
        var hInterval = interval / 2f;

        y = Locations.antiNegY(y, hInterval);

        for (var i = Locations.F360; i >= 0f; i -= interval) {

            var val1 = i - hInterval;
            var val2 = i + hInterval;

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
            var locs = location.split(":");
            var X = locs[0];
            var Y = locs[1];
            var Z = locs[2];
            var Yaw = locs[3];
            var Pitch = locs[4];
            var World = locs[5];
            var IgnoreDirection = locs[6];
            var x = Parser.parseDouble(X);
            var y = Parser.parseDouble(Y);
            var z = Parser.parseDouble(Z);
            var yaw = Parser.parseFloat(Yaw);
            var pitch = Parser.parseFloat(Pitch);
            var world = Parser.parseWorld(World);
            var ignoreDirection = Parser.parseBoolean(IgnoreDirection);
            var loc = new Location(world, x, y, z, yaw, pitch);
            if (oldLoc != null && ignoreDirection) loc.setDirection(oldLoc.getDirection());
            return loc;
        } catch (Throwable ignored) {
        }
        return oldLoc;
    }

    public static String serialize(Location loc, boolean ignoreDirection) {
        var x = loc.getX();
        var y = loc.getY();
        var z = loc.getZ();
        var yaw = loc.getYaw();
        var pitch = loc.getPitch();
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

    public static JsonObject toJson(Location loc, Boolean ignoreDirection) {
        var json = new JsonObject();
        json.addProperty("x", loc.getX());
        json.addProperty("y", loc.getY());
        json.addProperty("z", loc.getZ());
        json.addProperty("yaw", loc.getYaw());
        json.addProperty("pit", loc.getPitch());
        json.addProperty("wor", loc.getWorld().getName());
        if (ignoreDirection != null) {
            json.addProperty("igd", ignoreDirection);
        }
        return json;
    }

    public static Location fromJson(JsonObject json, Location oldLoc) {
        var x = json.get("x").getAsDouble();
        var y = json.get("y").getAsDouble();
        var z = json.get("z").getAsDouble();
        var yaw = json.get("yaw").getAsFloat();
        var pitch = json.get("pit").getAsFloat();
        var world = Bukkit.getWorld(json.get("wor").getAsString());
        var ignoreDirection = !json.has("igd") ? null : json.get("igd").getAsBoolean();
        var loc = new Location(world, x, y, z, yaw, pitch);
        if (oldLoc != null && ignoreDirection != null && ignoreDirection) {
            loc.setDirection(oldLoc.getDirection());
        }
        return loc;
    }

    public static Document toDocument(Location loc, boolean ignoreDirection) {
        return Document.newJsonDocument().append("x", loc.getX()).append("y", loc.getY()).append("z", loc.getZ()).append("yaw", loc.getYaw()).append("pit", loc.getPitch()).append("wor", loc.getWorld().getName()).append("igd", ignoreDirection);
    }

    public static Location fromDocument(Document doc, Location oldLoc) {
        var x = doc.getDouble("x");
        var y = doc.getDouble("y");
        var z = doc.getDouble("z");
        var yaw = doc.getFloat("yaw");
        var pit = doc.getFloat("pit");
        var world = Bukkit.getWorld(doc.getString("wor"));
        var ignoreDirection = doc.getBoolean("igd");
        var loc = new Location(world, x, y, z, yaw, pit);
        if (oldLoc != null && ignoreDirection) {
            loc.setDirection(oldLoc.getDirection());
        }
        return loc;
    }

}
