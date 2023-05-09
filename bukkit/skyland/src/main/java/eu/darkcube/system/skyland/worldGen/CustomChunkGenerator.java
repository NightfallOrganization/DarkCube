/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CustomChunkGenerator extends ChunkGenerator {

	//PerlinNoiseGenerator terrain = new PerlinNoiseGenerator(new Random());
	//PerlinNoiseGenerator details = new PerlinNoiseGenerator(new Random());

	SimplexOctaveGenerator islandGen = new SimplexOctaveGenerator(new Random(), 8);
	SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(), 8);
	SimplexOctaveGenerator details = new SimplexOctaveGenerator(new Random(), 16);
	SimplexOctaveGenerator biomeGen = new SimplexOctaveGenerator(new Random(1133423454), 6);

	HashMap<Integer, Integer> testResultHashMap = new HashMap<>();


	public CustomChunkGenerator() {
		generator.setScale(0.008D);
		details.setScale(0.02D);
		islandGen.setScale(0.008D);
		biomeGen.setScale(0.002D);

	}

	@Override
	public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull ChunkData chunkData) {



		//test


		int test = (int) ((biomeGen.noise(chunkX * 16, chunkZ * 16, 0.2D, 0.5D, true) +1 ) * 3D);


		//test end

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {



				int islandGenHeight = (int) ((islandGen.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5D, 0.5D, true) +1 ) * 50D);

				if (islandGenHeight > 5 &&islandGenHeight < 35){



					int currentHeight = (int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5D, 0.5D, true) +1 ) * 50D + 50D);
					currentHeight += (int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 + z, 2D, 0.5D, true)) * 50D + 50D)/4;
					chunkData.setBlock(x, currentHeight, z, Material.GRASS_BLOCK);
					chunkData.setBlock(x, currentHeight - 1, z, Material.DIRT);




					for (int i = currentHeight-2; i > currentHeight+2*islandGenHeight-73; i--){
						chunkData.setBlock(x, i, z, Material.STONE);
					}

				}

				//use this for the surface
				/*


				 */





				/*
				double d = (terrain.noise(x + chunkX * 16.0, z + chunkZ * 16.0, 8, 0.01, 1, true) + 1) /2;



				for (int y = chunkData.getMinHeight();
				     y < (chunkData.getMinHeight() + d*chunkData.getMaxHeight()) && y < chunkData.getMaxHeight(); y++) {
					chunkData.setBlock(x, y, z, Material.AMETHYST_BLOCK);
				}

				 */

			}
		}

		/*
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {

				Random rndm = new Random();
				int roll = rndm.nextInt(30);
				if (roll < 28) {
					chunkData.setBlock(i, 10, j, Material.DIRT);
				} else {
					chunkData.setBlock(i, 10, j, Material.AMETHYST_BLOCK);
				}

			}

		 */
	}

	@Override
	public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
		ArrayList<BlockPopulator> out = new ArrayList<>();
		out.add(new TreePopulators());
		out.add(new LootChestPopulator());
		return out;
	}

	@Override
	public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
		return new eu.darkcube.system.skyland.worldGen.BiomeProvider();
	}
}


