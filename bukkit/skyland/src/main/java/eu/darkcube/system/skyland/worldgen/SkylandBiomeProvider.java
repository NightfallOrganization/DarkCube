/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen;

import org.bukkit.block.Biome;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SkylandBiomeProvider extends org.bukkit.generator.BiomeProvider {

	SimplexOctaveGenerator biomeGen = new SimplexOctaveGenerator(new Random(1133423454), 6);
	SimplexOctaveGenerator biomeGen2 = new SimplexOctaveGenerator(new Random(124245), 6);

	public SkylandBiomeProvider(){
		biomeGen.setScale(0.002D);
		biomeGen2.setScale(0.002D);
	}
	@Override
	public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z) {
		//int test = (int) ((biomeGen.noise(x, z, 0.2D, 0.5D, true) +1 ));
		/*
		int test = (int) ((biomeGen.noise(x, z, 0.2D, 0.5D, true) +1 )*1);
		int test2 = (int) ((biomeGen2.noise(x, z, 0.2D, 0.5D, true) +1 )*1);


		switch (test){
			case 0: switch (test2){
				case 0: return SkylandBiomes.FOREST.getBiome();
				case 1: return SkylandBiomes.LAVA.getBiome();
			}
			case 1: switch (test2){
				case 0: return SkylandBiomes.TEST1.getBiome();
				case 1: return SkylandBiomes.TEST2.getBiome();
			}
			default: return SkylandBiomes.FOREST.getBiome();
		}

		 */

		return SkylandBiome.getBiome(x,z).getBiome();


	}



	@Override
	public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo) {
		ArrayList<Biome> out = new ArrayList<>();
		out.add(Biome.FOREST);
		return out;
	}
}
