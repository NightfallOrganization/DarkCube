/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class RayTrace {

    // origin = start position
    // direction = direction in which the raytrace will go
    public Vector origin;
    public Vector direction;

    public RayTrace(Vector origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    // get a point on the raytrace at X blocks away
    public Vector getPosition(double blocksAway) {
        return origin.clone().add(direction.clone().multiply(blocksAway));
    }

    // checks if a position is on contained within the position
    public boolean isOnLine(Vector position) {
        var t = (position.getX() - origin.getX()) / direction.getX();
        return position.getBlockY() == origin.getY() + (t * direction.getY()) && position.getBlockZ() == origin.getZ() + (t * direction.getZ());
    }

    // get all positions on a raytrace
    public ArrayList<Vector> traverse(double blocksAway, double accuracy) {
        var positions = new ArrayList<Vector>();
        for (double d = 0; d <= blocksAway; d += accuracy) {
            positions.add(getPosition(d));
        }
        return positions;
    }

    // intersection detection for current raytrace with return
    public Vector positionOfIntersection(Vector min, Vector max, double blocksAway, double accuracy) {
        var positions = traverse(blocksAway, accuracy);
        for (var position : positions) {
            if (intersects(position, min, max)) {
                return position;
            }
        }
        return null;
    }

    // intersection detection for current raytrace
    public boolean intersects(Vector min, Vector max, double blocksAway, double accuracy) {
        var positions = traverse(blocksAway, accuracy);
        for (var position : positions) {
            if (intersects(position, min, max)) {
                return true;
            }
        }
        return false;
    }

    // bounding box instead of vector
    public Vector positionOfIntersection(BoundingBox boundingBox, double blocksAway, double accuracy) {
        var positions = traverse(blocksAway, accuracy);
        for (var position : positions) {
            if (intersects(position, boundingBox.min, boundingBox.max)) {
                return position;
            }
        }
        return null;
    }

    // bounding box instead of vector
    public boolean intersects(BoundingBox boundingBox, double blocksAway, double accuracy) {
        var positions = traverse(blocksAway, accuracy);
        for (var position : positions) {
            if (intersects(position, boundingBox.min, boundingBox.max)) {
                return true;
            }
        }
        return false;
    }

    // general intersection detection
    public static boolean intersects(Vector position, Vector min, Vector max) {
        if (position.getX() < min.getX() || position.getX() > max.getX()) {
            return false;
        } else if (position.getY() < min.getY() || position.getY() > max.getY()) {
            return false;
        } else return !(position.getZ() < min.getZ()) && !(position.getZ() > max.getZ());
    }

    // debug / effects
    public void highlight(World world, double blocksAway, double accuracy) {
        for (var position : traverse(blocksAway, accuracy)) {
            world.playEffect(position.toLocation(world), Effect.COLOURED_DUST, 0);
        }
    }

}
