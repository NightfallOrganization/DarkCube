/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import java.util.EnumSet;

import org.bukkit.Location;

public class Vector3d implements IPosition {
	public static final Vector3d ZERO = new Vector3d(0.0D, 0.0D, 0.0D);
	public final double x;
	public final double y;
	public final double z;

	public static Vector3d unpack(int packed) {
		double d0 = (packed >> 16 & 255) / 255.0D;
		double d1 = (packed >> 8 & 255) / 255.0D;
		double d2 = (packed & 255) / 255.0D;
		return new Vector3d(d0, d1, d2);
	}

	public static Vector3d position(Location loc) {
		return new Vector3d(loc.getX(), loc.getY(), loc.getZ());
	}

	public static Vector3d copyCentered(Vector3i toCopy) {
		return new Vector3d(toCopy.getX() + 0.5D, toCopy.getY() + 0.5D,
						toCopy.getZ() + 0.5D);
	}

	public static Vector3d copy(Vector3i toCopy) {
		return new Vector3d(toCopy.getX(), toCopy.getY(), toCopy.getZ());
	}

	public static Vector3d copyCenteredHorizontally(Vector3i toCopy) {
		return new Vector3d(toCopy.getX() + 0.5D, toCopy.getY(),
						toCopy.getZ() + 0.5D);
	}

	public static Vector3d copyCenteredWithVerticalOffset(Vector3i toCopy,
					double verticalOffset) {
		return new Vector3d(toCopy.getX() + 0.5D,
						toCopy.getY() + verticalOffset, toCopy.getZ() + 0.5D);
	}

	public Vector3d(double xIn, double yIn, double zIn) {
		this.x = xIn;
		this.y = yIn;
		this.z = zIn;
	}

	public Vector3d(Vector3f vec) {
		this(vec.getX(), vec.getY(), vec.getZ());
	}

	public Vector3d subtractReverse(Vector3d vec) {
		return new Vector3d(vec.x - this.x, vec.y - this.y, vec.z - this.z);
	}

	public Vector3d normalize() {
		double d0 = MathHelper.sqrt(this.x * this.x + this.y * this.y
						+ this.z * this.z);
		return d0 < 1.0E-4D ? ZERO
						: new Vector3d(this.x / d0, this.y / d0, this.z / d0);
	}

	public double dotProduct(Vector3d vec) {
		return this.x * vec.x + this.y * vec.y + this.z * vec.z;
	}

	public Vector3d crossProduct(Vector3d vec) {
		return new Vector3d(this.y * vec.z - this.z * vec.y,
						this.z * vec.x - this.x * vec.z,
						this.x * vec.y - this.y * vec.x);
	}

	public Vector3d subtract(Vector3d vec) {
		return this.subtract(vec.x, vec.y, vec.z);
	}

	public Vector3d subtract(double x, double y, double z) {
		return this.add(-x, -y, -z);
	}

	public Vector3d add(Vector3d vec) {
		return this.add(vec.x, vec.y, vec.z);
	}

	public Vector3d add(double x, double y, double z) {
		return new Vector3d(this.x + x, this.y + y, this.z + z);
	}

	public boolean isWithinDistanceOf(IPosition pos, double distance) {
		return this.squareDistanceTo(pos.getX(), pos.getY(), pos.getZ()) < distance
						* distance;
	}

	public double distanceTo(Vector3d vec) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
	}

	public double squareDistanceTo(Vector3d vec) {
		double d0 = vec.x - this.x;
		double d1 = vec.y - this.y;
		double d2 = vec.z - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public double squareDistanceTo(double xIn, double yIn, double zIn) {
		double d0 = xIn - this.x;
		double d1 = yIn - this.y;
		double d2 = zIn - this.z;
		return d0 * d0 + d1 * d1 + d2 * d2;
	}

	public Vector3d scale(double factor) {
		return this.mul(factor, factor, factor);
	}

	public Vector3d inverse() {
		return this.scale(-1.0D);
	}

	public Vector3d mul(Vector3d vec) {
		return this.mul(vec.x, vec.y, vec.z);
	}

	public Vector3d mul(double factorX, double factorY, double factorZ) {
		return new Vector3d(this.x * factorX, this.y * factorY,
						this.z * factorZ);
	}

	public double length() {
		return MathHelper.sqrt(this.x * this.x + this.y * this.y
						+ this.z * this.z);
	}

	public double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	@Override
	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof Vector3d)) {
			return false;
		} else {
			Vector3d vector3d = (Vector3d) p_equals_1_;
			if (Double.compare(vector3d.x, this.x) != 0) {
				return false;
			} else if (Double.compare(vector3d.y, this.y) != 0) {
				return false;
			} else {
				return Double.compare(vector3d.z, this.z) == 0;
			}
		}
	}

	@Override
	public int hashCode() {
		long j = Double.doubleToLongBits(this.x);
		int i = (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.y);
		i = 31 * i + (int) (j ^ j >>> 32);
		j = Double.doubleToLongBits(this.z);
		return 31 * i + (int) (j ^ j >>> 32);
	}

	@Override
	public String toString() {
		return String.format("(%.2f, %.2f, %.2f)", this.x, this.y, this.z);
	}

	public Vector3d rotatePitch(float pitch) {
		float f = MathHelper.cos(pitch);
		float f1 = MathHelper.sin(pitch);
		double d0 = this.x;
		double d1 = this.y * f + this.z * f1;
		double d2 = this.z * f - this.y * f1;
		return new Vector3d(d0, d1, d2);
	}

	public Vector3d rotateYaw(float yaw) {
		float f = MathHelper.cos(yaw);
		float f1 = MathHelper.sin(yaw);
		double d0 = this.x * f + this.z * f1;
		double d1 = this.y;
		double d2 = this.z * f - this.x * f1;
		return new Vector3d(d0, d1, d2);
	}

	public Vector3d rotateRoll(float roll) {
		float f = MathHelper.cos(roll);
		float f1 = MathHelper.sin(roll);
		double d0 = this.x * f + this.y * f1;
		double d1 = this.y * f - this.x * f1;
		double d2 = this.z;
		return new Vector3d(d0, d1, d2);
	}

	public static Vector3d fromPitchYaw(Vector2f vec) {
		return fromPitchYaw(vec.x, vec.y);
	}

	public static Vector3d fromPitchYaw(float pitch, float yaw) {
		float f = MathHelper.cos(-yaw * ((float) Math.PI / 180F)
						- (float) Math.PI);
		float f1 = MathHelper.sin(-yaw * ((float) Math.PI / 180F)
						- (float) Math.PI);
		float f2 = -MathHelper.cos(-pitch * ((float) Math.PI / 180F));
		float f3 = MathHelper.sin(-pitch * ((float) Math.PI / 180F));
		return new Vector3d(f1 * f2, f3, f * f2);
	}

	public Vector3d align(EnumSet<Direction.Axis> axes) {
		double d0 = axes.contains(Direction.Axis.X)
						? (double) MathHelper.floor(this.x)
						: this.x;
		double d1 = axes.contains(Direction.Axis.Y)
						? (double) MathHelper.floor(this.y)
						: this.y;
		double d2 = axes.contains(Direction.Axis.Z)
						? (double) MathHelper.floor(this.z)
						: this.z;
		return new Vector3d(d0, d1, d2);
	}

	public double getCoordinate(Direction.Axis axis) {
		return axis.getCoordinate(this.x, this.y, this.z);
	}

	@Override
	public final double getX() {
		return this.x;
	}

	@Override
	public final double getY() {
		return this.y;
	}

	@Override
	public final double getZ() {
		return this.z;
	}
}
