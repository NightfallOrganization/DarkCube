/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen.Structures;

import eu.darkcube.system.skyland.worldGen.SkylandBiomes;
import org.bukkit.block.Biome;

public class SkylandStructureModifiers {
	SkylandBiomes biome;
	int rarity;
	int intensity;
	int islandThiccness;
	boolean spawnOnlyOnIsland;

	public SkylandStructureModifiers(SkylandBiomes biome, int rarity, int intensity, int islandThiccness, boolean spawnOnlyOnIsland) {
		this.biome = biome;
		this.rarity = rarity;
		this.intensity = intensity;
		this.islandThiccness =islandThiccness;
		this.spawnOnlyOnIsland = spawnOnlyOnIsland;
	}

	public SkylandBiomes getBiome() {
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
