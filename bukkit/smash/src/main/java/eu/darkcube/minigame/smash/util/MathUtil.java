package eu.darkcube.minigame.smash.util;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.World;

public class MathUtil {

	public static boolean canAttack(Player p, Player t, double maxDistance, double maxHeightDifference, float angle) {
		Location loc = p.getEyeLocation();
		Location target = t.getEyeLocation();
		if (p.equals(t)) {
			return false;
		}
		return canAttack(loc, target, maxDistance, maxHeightDifference, angle);
	}

	public static boolean canAttack(Location loc, Location target, double maxDistance, double maxHeightDifference,
			float angle) {
		double dist2 = loc.distanceSquared(target);
		System.out.println(1);
		if (dist2 > maxDistance * maxDistance) {
			return false;
		}
		System.out.println(2);
		double y = loc.getY();
		double ty = target.getY();
		boolean isInHeight = Math.abs(ty - y) - 0.9 < maxHeightDifference;
		if (!isInHeight)
			return false;
		System.out.println(3);
		float yaw = getRealYaw(loc.getYaw());
		double vx = target.getX() - loc.getX();
		double vy = target.getY() - loc.getY();
		double vz = target.getZ() - loc.getZ();
		Vector line = new Vector(vx, vy, vz);
		Location clone = loc.clone();
		clone.setDirection(line);
		float yaw1 = yaw - angle / 2;
		float yaw2 = yaw + angle / 2;
		boolean isInAngleRange = clone.getYaw() > yaw1 && clone.getYaw() < yaw2;
		if (!isInAngleRange)
			return false;

		System.out.println(4);
		int distInt = (int) loc.distance(target);
		if (distInt == 0) {
			return true;
		}
		System.out.println(5);
		BlockIterator it = new BlockIterator(loc.getWorld(), loc.toVector(), line, 0, distInt);
		while (it.hasNext()) {
			CraftBlock block = (CraftBlock) it.next();
			if (!block.isEmpty()) {
				World world = ((CraftWorld) block.getWorld()).getHandle();
				AxisAlignedBB bb = CraftMagicNumbers.getBlock(block).a(world,
						new BlockPosition(block.getX(), block.getY(), block.getZ()),
						world.getType(new BlockPosition(block.getX(), block.getY(), block.getZ())));
				if (bb.a % 1 == 0 && bb.b % 1 == 0 && bb.c % 1 == 0 && bb.d % 1 == 0 && bb.e % 1 == 0
						&& bb.f % 1 == 0) {
					return false;
				}
			}
		}
		System.out.println(6);
		return true;
	}

	public static float getRealYaw(float yaw) {
		yaw = yaw % 360;
		if (yaw < 0)
			yaw += 360;
		return yaw;
	}

	public static float getDisplayYaw(float yaw) {
		yaw = yaw % 360;
		if (yaw < -180)
			yaw += 360;
		if (yaw > 180)
			yaw -= 360;
		return yaw;
	}

}
