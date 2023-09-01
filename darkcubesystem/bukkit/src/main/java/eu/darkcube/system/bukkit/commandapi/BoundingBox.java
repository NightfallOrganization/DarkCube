/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import eu.darkcube.system.bukkit.version.BukkitVersion;
import eu.darkcube.system.commandapi.v3.Direction;
import eu.darkcube.system.commandapi.v3.MathHelper;
import eu.darkcube.system.commandapi.v3.Vector3d;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BoundingBox {

    private double minX, minY, minZ, maxX, maxY, maxZ;

    public BoundingBox(Entity entity) {
        double[] d = BukkitVersion.version().commandApiUtils().getEntityBB(entity);
        this.minX = d[0];
        this.minY = d[1];
        this.minZ = d[2];
        this.maxX = d[3];
        this.maxY = d[4];
        this.maxZ = d[5];
    }

    public BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2);
        this.maxY = Math.max(y1, y2);
        this.maxZ = Math.max(z1, z2);
    }

    private BoundingBox(double x1, double y1, double z1, double x2, double y2, double z2, double maxmodifierX, double maxmodifierY, double maxmodifierZ) {
        this.minX = Math.min(x1, x2);
        this.minY = Math.min(y1, y2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(x1, x2) + maxmodifierX;
        this.maxY = Math.max(y1, y2) + maxmodifierY;
        this.maxZ = Math.max(z1, z2) + maxmodifierZ;
    }

    public BoundingBox(Block pos) {
        this(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
    }

    public BoundingBox(Block pos1, Block pos2) {
        this(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ(), 1, 1, 1);
    }

    public BoundingBox(Vector3d min, Vector3d max) {
        this(min.x, min.y, min.z, max.x, max.y, max.z);
    }

    public static BoundingBox fromVector(Vector3d vector) {
        return new BoundingBox(vector.x, vector.y, vector.z, vector.x + 1.0D, vector.y + 1.0D, vector.z + 1.0D);
    }

    private static Direction calcSideHit(BoundingBox aabb, Vector3d start, double[] minDistance, double deltaX, double deltaY, double deltaZ) {
        Direction facing = null;
        if (deltaX > 1.0E-7D) {
            facing = BoundingBox.checkSideForHit(minDistance, deltaX, deltaY, deltaZ, aabb.minX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, Direction.WEST, start.x, start.y, start.z);
        } else if (deltaX < -1.0E-7D) {
            facing = BoundingBox.checkSideForHit(minDistance, deltaX, deltaY, deltaZ, aabb.maxX, aabb.minY, aabb.maxY, aabb.minZ, aabb.maxZ, Direction.EAST, start.x, start.y, start.z);
        }

        if (deltaY > 1.0E-7D) {
            facing = BoundingBox.checkSideForHit(minDistance, deltaY, deltaZ, deltaX, aabb.minY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, Direction.DOWN, start.y, start.z, start.x);
        } else if (deltaY < -1.0E-7D) {
            facing = BoundingBox.checkSideForHit(minDistance, deltaY, deltaZ, deltaX, aabb.maxY, aabb.minZ, aabb.maxZ, aabb.minX, aabb.maxX, Direction.UP, start.y, start.z, start.x);
        }

        if (deltaZ > 1.0E-7D) {
            facing = BoundingBox.checkSideForHit(minDistance, deltaZ, deltaX, deltaY, aabb.minZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, Direction.NORTH, start.z, start.x, start.y);
        } else if (deltaZ < -1.0E-7D) {
            facing = BoundingBox.checkSideForHit(minDistance, deltaZ, deltaX, deltaY, aabb.maxZ, aabb.minX, aabb.maxX, aabb.minY, aabb.maxY, Direction.SOUTH, start.z, start.x, start.y);
        }

        return facing;
    }

    private static Direction checkSideForHit(double[] minDistance, double distanceSide, double distanceOtherA, double distanceOtherB, double minSide, double minOtherA, double maxOtherA, double minOtherB, double maxOtherB, Direction hitSide, double startSide, double startOtherA, double startOtherB) {
        double d0 = (minSide - startSide) / distanceSide;
        double d1 = startOtherA + d0 * distanceOtherA;
        double d2 = startOtherB + d0 * distanceOtherB;
        if (0.0D < d0 && d0 < minDistance[0] && minOtherA - 1.0E-7D < d1 && d1 < maxOtherA + 1.0E-7D && minOtherB - 1.0E-7D < d2 && d2 < maxOtherB + 1.0E-7D) {
            minDistance[0] = d0;
            return hitSide;
        }
        return null;
    }

    public static BoundingBox withSizeAtOrigin(double xSize, double ySize, double zSize) {
        return new BoundingBox(-xSize / 2.0D, -ySize / 2.0D, -zSize / 2.0D, xSize / 2.0D, ySize / 2.0D, zSize / 2.0D);
    }

    public List<Entity> getEntitiesWithin(World world, EntityType type, Predicate<Entity> filter) {
        List<Entity> list = new ArrayList<>();
        for (Entity ent : world.getEntities()) {
            if (type == null || ent.getType() == type) {
                if (this.intersects(new BoundingBox(ent))) {
                    if (filter.test(ent)) {
                        list.add(ent);
                    }
                }
            }
        }
        return list;
    }

    public double getMin(Direction.Axis axis) {
        return axis.getCoordinate(this.minX, this.minY, this.minZ);
    }

    public double getMax(Direction.Axis axis) {
        return axis.getCoordinate(this.maxX, this.maxY, this.maxZ);
    }

    @Override public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (!(p_equals_1_ instanceof BoundingBox BoundingBox)) {
            return false;
        } else {
            if (Double.compare(BoundingBox.minX, this.minX) != 0) {
                return false;
            } else if (Double.compare(BoundingBox.minY, this.minY) != 0) {
                return false;
            } else if (Double.compare(BoundingBox.minZ, this.minZ) != 0) {
                return false;
            } else if (Double.compare(BoundingBox.maxX, this.maxX) != 0) {
                return false;
            } else if (Double.compare(BoundingBox.maxY, this.maxY) != 0) {
                return false;
            } else {
                return Double.compare(BoundingBox.maxZ, this.maxZ) == 0;
            }
        }
    }

    @Override public int hashCode() {
        long i = Double.doubleToLongBits(this.minX);
        int j = (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.minY);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.minZ);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxX);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxY);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxZ);
        return 31 * j + (int) (i ^ i >>> 32);
    }

    public BoundingBox contract(double x, double y, double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;
        if (x < 0.0D) {
            d0 -= x;
        } else if (x > 0.0D) {
            d3 -= x;
        }

        if (y < 0.0D) {
            d1 -= y;
        } else if (y > 0.0D) {
            d4 -= y;
        }

        if (z < 0.0D) {
            d2 -= z;
        } else if (z > 0.0D) {
            d5 -= z;
        }

        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public BoundingBox expand(Vector3d vector) {
        return this.expand(vector.x, vector.y, vector.z);
    }

    public BoundingBox expand(double x, double y, double z) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;
        if (x < 0.0D) {
            d0 += x;
        } else if (x > 0.0D) {
            d3 += x;
        }

        if (y < 0.0D) {
            d1 += y;
        } else if (y > 0.0D) {
            d4 += y;
        }

        if (z < 0.0D) {
            d2 += z;
        } else if (z > 0.0D) {
            d5 += z;
        }

        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public BoundingBox grow(double x, double y, double z) {
        double d0 = this.minX - x;
        double d1 = this.minY - y;
        double d2 = this.minZ - z;
        double d3 = this.maxX + x;
        double d4 = this.maxY + y;
        double d5 = this.maxZ + z;
        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public BoundingBox grow(double value) {
        return this.grow(value, value, value);
    }

    public BoundingBox intersect(BoundingBox other) {
        double d0 = Math.max(this.minX, other.minX);
        double d1 = Math.max(this.minY, other.minY);
        double d2 = Math.max(this.minZ, other.minZ);
        double d3 = Math.min(this.maxX, other.maxX);
        double d4 = Math.min(this.maxY, other.maxY);
        double d5 = Math.min(this.maxZ, other.maxZ);
        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public BoundingBox union(BoundingBox other) {
        double d0 = Math.min(this.minX, other.minX);
        double d1 = Math.min(this.minY, other.minY);
        double d2 = Math.min(this.minZ, other.minZ);
        double d3 = Math.max(this.maxX, other.maxX);
        double d4 = Math.max(this.maxY, other.maxY);
        double d5 = Math.max(this.maxZ, other.maxZ);
        return new BoundingBox(d0, d1, d2, d3, d4, d5);
    }

    public BoundingBox offset(double x, double y, double z) {
        return new BoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public BoundingBox offset(Block block) {
        Location loc = block.getLocation();
        return this.offset(new Vector3d(loc.getX(), loc.getY(), loc.getZ()));
    }

    public BoundingBox offset(Vector3d vec) {
        return this.offset(vec.x, vec.y, vec.z);
    }

    public boolean intersects(BoundingBox other) {
        return this.intersects(other.minX, other.minY, other.minZ, other.maxX, other.maxY, other.maxZ);
    }

    public boolean intersects(double x1, double y1, double z1, double x2, double y2, double z2) {
        return this.minX < x2 && this.maxX > x1 && this.minY < y2 && this.maxY > y1 && this.minZ < z2 && this.maxZ > z1;
    }

    public boolean intersects(Vector3d min, Vector3d max) {
        return this.intersects(Math.min(min.x, max.x), Math.min(min.y, max.y), Math.min(min.z, max.z), Math.max(min.x, max.x), Math.max(min.y, max.y), Math.max(min.z, max.z));
    }

    public boolean contains(Vector3d vec) {
        return this.contains(vec.x, vec.y, vec.z);
    }

    public boolean contains(double x, double y, double z) {
        return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
    }

    public double getAverageEdgeLength() {
        double d0 = this.getXSize();
        double d1 = this.getYSize();
        double d2 = this.getZSize();
        return (d0 + d1 + d2) / 3.0D;
    }

    public double getXSize() {
        return this.maxX - this.minX;
    }

    public double getYSize() {
        return this.maxY - this.minY;
    }

    public double getZSize() {
        return this.maxZ - this.minZ;
    }

    public BoundingBox shrink(double value) {
        return this.grow(-value);
    }

    public Optional<Vector3d> rayTrace(Vector3d from, Vector3d to) {
        double[] adouble = new double[]{1.0D};
        double d0 = to.x - from.x;
        double d1 = to.y - from.y;
        double d2 = to.z - from.z;
        Direction direction = BoundingBox.calcSideHit(this, from, adouble, d0, d1, d2);
        if (direction == null) {
            return Optional.empty();
        }
        double d3 = adouble[0];
        return Optional.of(from.add(d3 * d0, d3 * d1, d3 * d2));
    }

    @Override public String toString() {
        return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ) || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }

    public Vector3d getCenter() {
        return new Vector3d(MathHelper.lerp(0.5D, this.minX, this.maxX), MathHelper.lerp(0.5D, this.minY, this.maxY), MathHelper.lerp(0.5D, this.minZ, this.maxZ));
    }

}
