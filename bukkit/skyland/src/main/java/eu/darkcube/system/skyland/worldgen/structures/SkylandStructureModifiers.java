/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen.structures;

import eu.darkcube.system.skyland.worldgen.SkylandBiome;
import org.bukkit.util.Vector;

public class SkylandStructureModifiers {
	SkylandBiome biome;
	int rarity;
	int intensity;
	int islandThiccness;
	boolean spawnOnlyOnIsland;
	Vector offset;


	public SkylandStructureModifiers(SkylandBiome biome, int rarity, int intensity, int islandThiccness, boolean spawnOnlyOnIsland, Vector offset) {
		this.biome = biome;
		this.rarity = rarity;
		this.intensity = intensity;
		this.islandThiccness =islandThiccness;
		this.spawnOnlyOnIsland = spawnOnlyOnIsland;
		this.offset = offset;
	}

	public SkylandBiome getBiome() {
		return biome;
	}

	public int getIntensity() {
		return intensity;
	}

	public int getRarity() {
		return rarity;
	}

	public int getIslandThiccness() {
		return islandThiccness;
	}

	public boolean isSpawnOnlyOnIsland() {
		return spawnOnlyOnIsland;
	}
}
