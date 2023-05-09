/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BiomePopulator extends BlockPopulator {

	@Override
	public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull LimitedRegion limitedRegion) {
		for (int x = 0; x < 16; x++) {
			for (int z =0; z < 16; z++) {
				for (int y = worldInfo.getMinHeight(); y < worldInfo.getMaxHeight(); y++) {
					limitedRegion.getWorld().setBiome(x,y,z, Biome.FOREST);
				}
			}
		}
	}
}
