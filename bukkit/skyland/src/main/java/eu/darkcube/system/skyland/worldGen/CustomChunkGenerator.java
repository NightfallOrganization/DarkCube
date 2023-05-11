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

	SimplexOctaveGenerator islandThiccnessNoise = new SimplexOctaveGenerator(new Random(), 8);
	SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(), 8);
	SimplexOctaveGenerator details = new SimplexOctaveGenerator(new Random(113445), 16);
	SimplexOctaveGenerator spikeGen = new SimplexOctaveGenerator(new Random(), 3);

	HashMap<Integer, Integer> testResultHashMap = new HashMap<>();

	public CustomChunkGenerator() {
		//generator.setScale(0.008D);
		//details.setScale(0.02D);
		generator.setScale(0.0028D);
		details.setScale(0.02D);
		islandThiccnessNoise.setScale(0.008D);
		spikeGen.setScale(0.002D);

	}

	@Override
	public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull ChunkData chunkData) {


		//test end

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {

				int islandGenHeight = getIslandHeight(chunkX * 16 + x, chunkZ * 16 + z);
				SkylandBiomes skylandBiomes = SkylandBiomes.getBiome(chunkX * 16 + x, chunkZ * 16 + z);

				if (islandGenHeight < 35) {

					/*
					//int currentHeight = (int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 +
					// z, 0.5D, 0.5D, true) +1 ) * 50D + 50D);
					int currentHeight =
							(int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5D, 0.2D,
									true) + 1) * 50D + 50D);
					//currentHeight += (int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 + z,
					// 2D, 0.5D, true)) * 50D + 50D)/4;
					currentHeight +=
							(int) ((details.noise(chunkX * 16 + x, chunkZ * 16 + z, 1D, 0.2D,
									true)) * 50D + 50D) / 4;
					 */
					//int currentHeight = getCurrentHeight(chunkX * 16 + x, chunkZ * 16 + z);

					int currentHeight = getFinalTopY(chunkX * 16 + x, chunkZ * 16 + z);



					chunkData.setBlock(x, currentHeight, z,
							skylandBiomes.getBiomeBlock().getNextSurfaceBlock(random).getBlock());
					chunkData.setBlock(x, currentHeight - 1, z,
							skylandBiomes.getBiomeBlock().getNextBelowSurfaceBlock(random)
									.getBlock());
					chunkData.setBlock(x, currentHeight - 2, z,
							skylandBiomes.getBiomeBlock().getNextBelowSurfaceBlock(random)
									.getBlock());

					double spikes =
							((spikeGen.noise(chunkX * 16 + x, chunkZ * 16 + z, 5D, 4D, true) + 1)
									* 31D);

					if (spikes < 30) {
						spikes = 0;
					} else {
						spikes -= 30;
					}
					//-spikes
					for (int i = currentHeight - 3; i > currentHeight + 2 * islandGenHeight - 73; i--) {
						skylandBiomes = SkylandBiomes.getBiome(chunkX * 16 + x, chunkZ * 16 + z);
						chunkData.setBlock(x, i, z,
								skylandBiomes.getBiomeBlock().getNextStone(random).getBlock());
					}

					for (int i = currentHeight + 2 * islandGenHeight - 73;
					     i > currentHeight + 2 * islandGenHeight - 73 - spikes; i--) {
						skylandBiomes = SkylandBiomes.getBiome(chunkX * 16 + x, chunkZ * 16 + z);
						chunkData.setBlock(x, i, z, Material.GREEN_WOOL);
					}

					spikes = ((spikeGen.noise(chunkX * 16 + x, chunkZ * 16 + z, 5D, 4D, true) + 1)
							* 31D);

					if (skylandBiomes.equals(SkylandBiomes.LAVA)) {
						if (spikes < 30) {
							spikes = 0;
						} else {
							spikes -= 30;
						}

						for (int i = currentHeight + 1; i < currentHeight + spikes; i++) {
							skylandBiomes = SkylandBiomes.getBiome(chunkX * 16 + x, chunkZ * 16 + z);

							chunkData.setBlock(x, i, z, Material.REDSTONE_BLOCK);
						}

					}

				}

				//use this for the surface
				/*


				 */





				/*
				double d = (terrain.noise(x + chunkX * 16.0, z + chunkZ * 16.0, 8, 0.01, 1, true)
				+ 1) /2;



				for (int y = chunkData.getMinHeight();
				     y < (chunkData.getMinHeight() + d*chunkData.getMaxHeight()) && y < chunkData
				     .getMaxHeight(); y++) {
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

	public int getRawTopY(int x, int z, SkylandBiomes biomes) {

		int baseNoise = (int) ((generator.noise(x, z, biomes.getBiomeGenModifiers().terrainFrequency, biomes.getBiomeGenModifiers().terrainAmplitude,
				true) + 1) * biomes.getBiomeGenModifiers().maxTerrainHeight + biomes.getBiomeGenModifiers().minTerrainHeight);

		BiomeGenModifiers biomeGenModifier = biomes.getBiomeGenModifiers();

		int details =  (int) ((this.details.noise(x,z, biomeGenModifier.detailsFrequency,
				biomeGenModifier.detailsAmplitude,
				true)) * biomeGenModifier.maxDetailsHeight + biomeGenModifier.minDetailsHeight) / biomeGenModifier.detailsImpact;


		return baseNoise + details;
	}
	public int getFinalTopY(int x, int z) {
		int iterpolRad = 5;

		int sum = 0;
		for (int dx = -iterpolRad; dx <= iterpolRad; dx++)  {
			for (int dz = -iterpolRad; dz <= iterpolRad; dz++) {
				sum += getRawTopY(x, z, SkylandBiomes.getBiome(x + dx, z + dz));
			}
		}
		return sum / ((iterpolRad*2+1) * (iterpolRad*2+1));
	}

	public int getCurrentHeightTemp(SkylandBiomes skylandBiomes, int x, int z){
		//int currentHeight = (int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 +
		// z, 0.5D, 0.5D, true) +1 ) * 50D + 50D);

		BiomeGenModifiers biomeGenModifier = skylandBiomes.getBiomeGenModifiers();

		int currentHeight =
				(int) ((generator.noise(x, z, biomeGenModifier.terrainFrequency, biomeGenModifier.terrainAmplitude,
						true) + 1) * biomeGenModifier.maxTerrainHeight + biomeGenModifier.minTerrainHeight);
		//currentHeight += (int) ((generator.noise(chunkX * 16 + x, chunkZ * 16 + z,
		// 2D, 0.5D, true)) * 50D + 50D)/4;
		currentHeight +=
				(int) ((details.noise(x,z, biomeGenModifier.detailsFrequency,
						biomeGenModifier.detailsAmplitude,
						true)) * biomeGenModifier.maxDetailsHeight + biomeGenModifier.minDetailsHeight) / biomeGenModifier.detailsImpact;




		int islandGenHeight = getIslandHeight(x, z);

		if (islandGenHeight >= 29){
			currentHeight = (int) (currentHeight - 0.67*(islandGenHeight-35) - 0.67*6);
		}

		return currentHeight;
	}

	public int getCurrentHeight(int x, int z){


		return getCurrentHeightTemp(SkylandBiomes.getBiome(x, z), x ,z);
	}














	public int getIslandHeight(int x, int z){
		int islandGenHeight =
				(int) ((islandThiccnessNoise.noise(x/2, z/2, 0.5D, 0.5D, true)
						+ 1) * 50D);
		return islandGenHeight;
	}

	@Override
	public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull ChunkData chunkData) {
		super.generateSurface(worldInfo, random, chunkX, chunkZ, chunkData);
		//todo
	}

	@Override
	public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
		ArrayList<BlockPopulator> out = new ArrayList<>();
		//out.add(new TreePopulators());
		out.add(new LootChestPopulator());

		return out;
	}

	SkylandBiomeProvider biomeProvider = new SkylandBiomeProvider();

	@Override
	public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
		return biomeProvider;
	}
}


