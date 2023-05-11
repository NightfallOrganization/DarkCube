/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import org.bukkit.Material;

public class BiomeBLock {
	Material block;
	int weight;

	public BiomeBLock(Material block, int weight) {
		this.block = block;
		this.weight = weight;
	}
	public BiomeBLock(Material block) {
		this.block = block;
		this.weight = 1;
	}

	public int getWeight() {
		return weight;
	}

	public Material getBlock() {
		return block;
	}
}
