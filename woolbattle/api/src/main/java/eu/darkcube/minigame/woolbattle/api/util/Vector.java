/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

public record Vector(double x, double y, double z) {
    public Vector() {
        this(0, 0, 0);
    }

    public Vector plus(Vector vec) {
        return new Vector(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vector minus(Vector vec) {
        return new Vector(x - vec.x, y - vec.y, z - vec.z);
    }

    public Vector mul(Vector vec) {
        return new Vector(x * vec.x, y * vec.y, z * vec.z);
    }

    public Vector div(Vector vec) {
        return new Vector(x / vec.x, y / vec.y, z / vec.z);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public double lengthSquared() {
        return x * x + y * y + z * z;
    }

    public double distance(Vector o) {
        return Math.sqrt(distanceSquared(o));
    }

    public double distanceSquared(Vector o) {
        return (x - o.x) * (x - o.x) + (y - o.y) * (y - o.y) + (z - o.z) * (z - o.z);
    }

    public Vector mid(Vector o) {
        return new Vector((x + o.x) / 2, (y + o.y) / 2, (z + o.z) / 2);
    }

    public float angle(Vector o) {
        var dot = dot(o) / (length() * o.length());
        return (float) Math.acos(dot);
    }

    public double dot(Vector o) {
        return x * o.x + y * o.y + z * o.z;
    }

    public Vector mul(double m) {
        return new Vector(x * m, y * m, z * m);
    }

    public Vector div(double m) {
        return new Vector(x / m, y / m, z / m);
    }

    public Vector cross(Vector o) {
        return new Vector(y * o.z - o.y * z, z * o.x - o.z * x, x * o.y - o.x * y);
    }

    public Vector normalized() {
        var length = length();
        return new Vector(x / length, y / length, z / length);
    }

    // TODO move to constructor when Statements before super() is released
    public static Vector fromEuler(float yaw, float pitch) {
        var rx = Math.toRadians(yaw);
        var ry = Math.toRadians(pitch);
        var y = -Math.sin(ry);
        var xz = Math.cos(ry);
        var x = -xz * Math.sin(rx);
        var z = xz * Math.cos(rx);
        return new Vector(x, y, z);
    }
}
