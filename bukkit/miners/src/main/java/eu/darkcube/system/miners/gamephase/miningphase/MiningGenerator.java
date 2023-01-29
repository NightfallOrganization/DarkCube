/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.miningphase;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import eu.darkcube.system.miners.Miners;

public class MiningGenerator {

	private static final int CUBE_SIZE = 65;
	private static final Random RANDOM = new Random();
	private static final Vector[] DIRECTIONS = new Vector[] { new Vector(1, 0, 0), new Vector(1, 0, 1),
			new Vector(-1, 0, 0), new Vector(-1, 0, -1), new Vector(-1, 0, 1), new Vector(1, 0, -1),
			new Vector(0, 0, 1), new Vector(0, 0, -1), new Vector(0, 1, 0), new Vector(1, 1, 0), new Vector(1, 1, 1),
			new Vector(-1, 1, 0), new Vector(-1, 1, -1), new Vector(-1, 1, 1), new Vector(1, 1, -1),
			new Vector(0, 1, 1), new Vector(0, 1, -1), new Vector(0, -1, 0), new Vector(1, -1, 0), new Vector(1, -1, 1),
			new Vector(-1, -1, 0), new Vector(-1, -1, -1), new Vector(-1, -1, 1), new Vector(1, -1, -1),
			new Vector(0, -1, 1), new Vector(0, -1, -1) };

	public static Vector pickRandomBlock() {
		return new Vector(RANDOM.nextInt(CUBE_SIZE) + 1, RANDOM.nextInt(CUBE_SIZE) + 1, RANDOM.nextInt(CUBE_SIZE) + 1);
	}

	public static boolean generateOreVein(Vector startOffset, Material material, int size) {
		if (!placeBlockForAll(startOffset, material))
			return false;
		int count = size - 1;
		int tries = 0;
		while (count > 0 && tries < 15) {
			if (placeBlockForAll(startOffset.clone().add(DIRECTIONS[RANDOM.nextInt(26)]), material)) {
				count--;
				tries = 0;
			} else
				tries++;
		}
		return true;
	}

	public static boolean placeBlockForAll(Vector offset, Material material) {
		if (!placeBlock(Miningphase.getCubeBase(1).clone().add(offset), material))
			return false;
		for (int i = 2; i <= Miners.getMinersConfig().TEAM_COUNT; i++) {
			placeBlock(Miningphase.getCubeBase(i).clone().add(offset), material);
		}
		return true;
	}

	public static boolean placeBlock(Location location, Material material) {
		if (!location.getBlock().getType().equals(Material.STONE))
			return false;
		location.getBlock().setType(material);
		return true;
	}

}
