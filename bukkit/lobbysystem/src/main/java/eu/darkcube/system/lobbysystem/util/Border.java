/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonDeserializer;
import eu.darkcube.system.libs.com.google.gson.JsonPrimitive;
import eu.darkcube.system.libs.com.google.gson.JsonSerializer;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class Border implements Serializable, Cloneable {

    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(Location.class, (JsonSerializer<Location>) (loc, type, con) -> new JsonPrimitive(Locations.serialize(loc))).registerTypeAdapter(Location.class, (JsonDeserializer<Location>) (json, type, con) -> Locations.deserialize(json.getAsString(), null)).create();
    public static final PersistentDataType<Border> TYPE = PersistentDataTypes.create(jsonElement -> GSON.fromJson(jsonElement, Border.class), GSON::toJsonTree, Border::clone);

    private Shape shape;
    private double radius;
    private Location loc;
    private Location loc2;

    public Border(Shape shape, double radius, Location loc, Location loc2) {
        this.shape = shape;
        this.radius = radius;
        this.loc = loc;
        this.loc2 = loc2;
    }

    @Override
    public Border clone() {
        return new Border(shape, radius, loc, loc2);
    }

    public boolean isInside(Entity ent) {
        return isInside(ent.getLocation());
    }

    public boolean isInside(Location o) {
        boolean inside = false;
        if (this.loc.getWorld() != o.getWorld()) return false;
        switch (this.shape) {
            case CIRCLE:
                inside = this.loc.distance(o) < this.radius;
                break;
            case RECTANGLE:
                if (this.loc2 == null) return true;
                double sx = Math.min(this.loc.getX(), this.loc2.getX());
                double sy = Math.min(this.loc.getY(), this.loc2.getY());
                double sz = Math.min(this.loc.getZ(), this.loc2.getZ());
                double bx = Math.max(this.loc.getX(), this.loc2.getX());
                double by = Math.max(this.loc.getY(), this.loc2.getY());
                double bz = Math.max(this.loc.getZ(), this.loc2.getZ());
                double x = o.getX();
                double y = o.getY();
                double z = o.getZ();
                // return (sx < o.getX() && o.getX() > bx) && (sy < o.getY() && o.getY() > by)
                // && (sz < o.getZ() && o.getZ() > bz);
                inside = sx < x && bx > x && sz < z && bz > z && sy < y && by > y;
                break;
        }
        return inside;
    }

    public Location getLoc1() {
        return this.loc.clone();
    }

    public Location getLoc2() {
        return this.loc2 == null ? null : this.loc2.clone();
    }

    public double getRadius() {
        return this.radius;
    }

    public Shape getShape() {
        return this.shape;
    }

    public boolean isOutside(Entity ent) {
        return !this.isInside(ent);
    }

    @Override
    public String serialize() {
        return Border.GSON.toJson(this);
    }

    public void boost(Entity ent) {
        Location mid = this.getMid();
        double x = mid.getX() - ent.getLocation().getX();
        // double y = ent.getLocation().getY() > Math.max(this.loc.getY(), this.loc2.getY()) ?
        // mid.getY() - ent.getLocation().getY()
        // : 2;
        double ymax;
        switch (this.shape) {
            case CIRCLE:
                ymax = this.loc.getY() + this.radius;
                break;
            case RECTANGLE:
                ymax = Math.max(this.loc.getY(), this.loc2.getY());
                break;
            default:
                throw new UnsupportedOperationException();
        }
        double y = ent.getLocation().getY() > ymax ? mid.getY() - ent.getLocation().getY() : 2;
        double z = mid.getZ() - ent.getLocation().getZ();
        ent.setVelocity(new Vector(x, y, z).normalize().multiply(2));
    }

    public Location getMid() {
        switch (this.shape) {
            case CIRCLE:
                return this.loc;
            case RECTANGLE:
                return this.loc.clone().add(this.loc2).multiply(.5);
        }
        return this.loc;
    }

    public static enum Shape {
        RECTANGLE,
        CIRCLE
    }

}
