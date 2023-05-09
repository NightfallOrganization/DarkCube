/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import org.bukkit.block.Biome;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BiomeProvider extends org.bukkit.generator.BiomeProvider {
	@Override
	public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
		return Biome.FOREST;
	}

	@Override
	public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
		ArrayList<Biome> out = new ArrayList<>();
		out.add(Biome.FOREST);
		return out;
	}
}
