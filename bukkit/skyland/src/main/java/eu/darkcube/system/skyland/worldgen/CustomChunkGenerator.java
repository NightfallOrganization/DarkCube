/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CustomChunkGenerator extends ChunkGenerator {

	//PerlinNoiseGenerator terrain = new PerlinNoiseGenerator(new Random());
	//PerlinNoiseGenerator details = new PerlinNoiseGenerator(new Random());

	int seed = 1123214124;
	SimplexOctaveGenerator islandThiccnessNoise = new SimplexOctaveGenerator(new Random(seed), 8);
	SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(seed), 8);
	SimplexOctaveGenerator details = new SimplexOctaveGenerator(new Random(seed), 16);
	SimplexOctaveGenerator spikeGen = new SimplexOctaveGenerator(new Random(seed), 3);
	SimplexOctaveGenerator biomeBorderGen = new SimplexOctaveGenerator(new Random(seed), 100);


	int iterpolRad = 5;

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

				int islandGenHeight = getIslandThickess(chunkX * 16 + x, chunkZ * 16 + z);
				SkylandBiome skylandBiome = SkylandBiome.getBiome(chunkX * 16 + x, chunkZ * 16 + z);



				int xNoise = (int) ((biomeBorderGen.noise(chunkX * 16 + x, chunkZ * 16 + z,1, 10, 1, true)+1) * 10000);
				int zNoise = (int) ((biomeBorderGen.noise(chunkX * 16 + x, chunkZ * 16 + z,2, 10, 1, true)+1)  * 10000);
				if (xNoise < 0){
					xNoise = xNoise*-2;
				}
				if (zNoise < 0){
					zNoise = zNoise*-2;
				}

				xNoise = xNoise % (iterpolRad+1);
				xNoise-= iterpolRad;
				zNoise = zNoise % (iterpolRad+1);
				zNoise-= iterpolRad;


				SkylandBiome skylandBiomeBlock = SkylandBiome.getBiome( (chunkX * 16 + x + xNoise),  (chunkZ * 16 + z + zNoise));





				if (isIsland(chunkX * 16 + x, chunkZ * 16 + z)) {

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
							skylandBiomeBlock.getBiomeBlock().getNextSurfaceBlock(random).getBlock());
					chunkData.setBlock(x, currentHeight - 1, z,
							skylandBiomeBlock.getBiomeBlock().getNextBelowSurfaceBlock(random)
									.getBlock());
					chunkData.setBlock(x, currentHeight - 2, z,
							skylandBiomeBlock.getBiomeBlock().getNextBelowSurfaceBlock(random)
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
						//skylandBiome = SkylandBiome.getBiome(chunkX * 16 + x, chunkZ * 16 + z);
						chunkData.setBlock(x, i, z,
								skylandBiomeBlock.getBiomeBlock().getNextStone(random).getBlock());
					}

					for (int i = currentHeight + 2 * islandGenHeight - 73;
					     i > currentHeight + 2 * islandGenHeight - 73 - spikes; i--) {
						//skylandBiome = SkylandBiome.getBiome(chunkX * 16 + x, chunkZ * 16 + z);
						chunkData.setBlock(x, i, z, skylandBiomeBlock.getBiomeBlock().getNextStone(random).getBlock());
					}

					spikes = ((spikeGen.noise(chunkX * 16 + x, chunkZ * 16 + z, 5D, 4D, true) + 1)
							* 31D);

					if (skylandBiome.equals(SkylandBiome.LAVA)) {
						if (spikes < 30) {
							spikes = 0;
						} else {
							spikes -= 30;
						}

						for (int i = currentHeight + 1; i < currentHeight + spikes; i++) {
							//skylandBiome = SkylandBiome.getBiome(chunkX * 16 + x, chunkZ * 16 + z);

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


	}

	/**
	 * 
	 * @returns whether the block at x z is part of an island
	 */
	public boolean isIsland(int x, int z){
		boolean isVoidIslands = true;
		if (getIslandThickess(x, z) < 35 || !isVoidIslands) {
			return true;
		}

		return false;
	}
	public int getIslandIntensity(int x, int z){
		return getIslandThickess(x, z) -35;
	}

	public int getIslandThickess(int x, int z){
		int islandGenHeight =
				(int) ((islandThiccnessNoise.noise(x/2, z/2, 0.5D, 0.5D, true)
						+ 1) * 150D);
		return islandGenHeight;
	}


	public int getRawTopY(int x, int z, SkylandBiome biomes) {

		int baseNoise = (int) ((generator.noise(x, z, biomes.getBiomeGenModifiers().terrainFrequency, biomes.getBiomeGenModifiers().terrainAmplitude,
				true) + 1) * biomes.getBiomeGenModifiers().maxTerrainHeight + biomes.getBiomeGenModifiers().minTerrainHeight);

		BiomeGenModifiers biomeGenModifier = biomes.getBiomeGenModifiers();

		int details =  (int) ((this.details.noise(x,z, biomeGenModifier.detailsFrequency,
				biomeGenModifier.detailsAmplitude,
				true)) * biomeGenModifier.maxDetailsHeight + biomeGenModifier.minDetailsHeight) / biomeGenModifier.detailsImpact;


		return baseNoise + details;
	}
	public int getFinalTopY(int x, int z) {

		int sum = 0;
		for (int dx = -iterpolRad; dx <= iterpolRad; dx++)  {
			for (int dz = -iterpolRad; dz <= iterpolRad; dz++) {
				sum += getRawTopY(x, z, SkylandBiome.getBiome(x + dx, z + dz));
			}
		}
		int out = sum / ((iterpolRad*2+1) * (iterpolRad*2+1));

		int islandGenHeight = getIslandThickess(x, z);

		if (islandGenHeight >= 29){
			out = (int) (out - 0.67*(islandGenHeight-35) - 0.67*6);
		}

		return out;
	}



	/**
	 * This method uses the given x and z value to generate the island's thiccness (or existance) at this point.
	 * This method only concerns itself with primary terrain generation. (Not spikes)
	 * @param x
	 * @param z
	 * @return
	 */


	@Override
	public void generateSurface(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull ChunkData chunkData) {
		super.generateSurface(worldInfo, random, chunkX, chunkZ, chunkData);
		//todo
	}

	@Override
	public @NotNull List<BlockPopulator> getDefaultPopulators(@NotNull World world) {
		ArrayList<BlockPopulator> out = new ArrayList<>();
		out.add(new TreePopulator());
		out.add(new LootChestPopulator());

		return out;
	}

	SkylandBiomeProvider biomeProvider = new SkylandBiomeProvider();

	@Override
	public @Nullable BiomeProvider getDefaultBiomeProvider(@NotNull WorldInfo worldInfo) {
		return biomeProvider;
	}


	public boolean shouldGenerateObject(SimplexOctaveGenerator generator, int x, int z, int rarity, int minIntensity) {
		double roll = (generator.noise(x, z, 0.5D, 0.5D, true) + 1);
		return ((int) (roll * rarity)) % rarity == 0
				&& SkylandBiome.getBiomeIntensity(x, z) > minIntensity;
	}



}


