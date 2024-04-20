/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BoundingBox {
    // min and max points of hit box
    public Vector max;
    public Vector min;
    public AxisAlignedBB box;

    BoundingBox(Vector min, Vector max) {
        this.max = max;
        this.min = min;
        box = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public BoundingBox(Player player) {
        var ep = ((CraftPlayer) player).getHandle();
        this.box = ep.getBoundingBox();
        this.min = new Vector(box.a, box.b, box.c);
        this.max = new Vector(box.d, box.e, box.f);
    }

    public BoundingBox(Player player, Location loc) {
        var delta = loc.clone().subtract(player.getLocation());
        var ep = ((CraftPlayer) player).getHandle();
        var box = ep.getBoundingBox();
        var a = box.a + delta.getX();
        var b = box.b + delta.getY();
        var c = box.c + delta.getZ();
        var d = box.d + delta.getX();
        var e = box.e + delta.getY();
        var f = box.f + delta.getZ();
        this.box = new AxisAlignedBB(a, b, c, d, e, f);

        this.min = new Vector(this.box.a, this.box.b, this.box.c);
        this.max = new Vector(this.box.d, this.box.e, this.box.f);
    }

    // gets min and max point of block
    // ** 1.8 and earlier **
    public BoundingBox(Block block) {
        var blockData = ((CraftWorld) block.getWorld()).getHandle().getType(new BlockPosition(block.getX(), block.getY(), block.getZ()));
        var blockNative = blockData.getBlock();
        blockNative.updateShape(((CraftWorld) block.getWorld()).getHandle(), new BlockPosition(block.getX(), block.getY(), block.getZ()));
        min = new Vector(block.getX() + blockNative.B(), block.getY() + blockNative.D(), block.getZ() + blockNative.F());
        max = new Vector(block.getX() + blockNative.C(), block.getY() + blockNative.E(), block.getZ() + blockNative.G());
        box = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }

    public BoundingBox shrink(double dist) {
        return this.shrink(dist, dist, dist);
    }

    public BoundingBox shrink(double distx, double disty, double distz) {
        box = box.shrink(distx, disty, distz);
        min.setX(min.getX() + distx);
        min.setY(min.getY() + disty);
        min.setZ(min.getZ() + distz);
        max.setX(max.getX() - distx);
        max.setY(max.getY() - disty);
        max.setZ(max.getZ() - distz);
        return this;
    }

    public boolean collidesVertically(BoundingBox box) {
        return !(box.max.getY() <= min.getY()) && !(box.min.getY() > max.getY());
    }

    public boolean collides(BoundingBox box) {
        return this.box.b(box.box);
    }

    public Vector midPoint() {
        return max.clone().add(min).multiply(0.5);
    }
}
