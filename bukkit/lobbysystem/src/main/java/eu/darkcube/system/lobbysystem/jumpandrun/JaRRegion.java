/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;

public class JaRRegion {

	final World world;
	final int x;
	final int y;
	final int z;
	final int widthX;
	final int height;
	final int widthZ;

	public JaRRegion(World world, int x, int y, int z, int widthX, int height, int widthZ) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.widthX = widthX;
		this.height = height;
		this.widthZ = widthZ;
	}

	public boolean contains(Block block) {
		return block.getWorld().equals(world) && block.getX() >= x && block.getX() < x + widthX
				&& block.getY() >= y && block.getY() < y + height && block.getZ() >= z
				&& block.getZ() < z + widthZ;
	}

	@Override
	public String toString() {
		return "JaRRegion [world=" + this.world.getName() + ", x=" + this.x + ", y=" + this.y
				+ ", z=" + this.z + ", widthX=" + this.widthX + ", height=" + this.height
				+ ", widthZ=" + this.widthZ + "]";
	}

	public String serialize() {
		return String.format("%s:%s:%s:%s:%s:%s:%s", world.getName(), x, y, z, widthX, widthZ,
				height);
	}

	public static JaRRegion deserialize(String data) {
		String[] a = data.split(":");
		int x = i(a[1]);
		int y = i(a[2]);
		int z = i(a[3]);
		int wx = i(a[4]);
		int h = i(a[5]);
		int wz = i(a[6]);
		World w = Bukkit.getWorld(a[0]);
		return new JaRRegion(w, x, y, z, wx, h, wz);
	}

	private static int i(String s) {
		return Integer.parseInt(s);
	}

}
