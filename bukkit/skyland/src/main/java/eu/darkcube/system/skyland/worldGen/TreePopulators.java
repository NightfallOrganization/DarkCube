/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class TreePopulators extends BlockPopulator {

	SimplexOctaveGenerator islandGen = new SimplexOctaveGenerator(new Random(), 8);

	public TreePopulators() {
		islandGen.setScale(1D);
	}

	@Override
	public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull LimitedRegion limitedRegion) {



		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {

				double roll = (islandGen.noise(chunkX * 16 + x, chunkZ * 16 + z, 0.5D, 0.5D, true) + 1);
				int treeFreq = 2; // tree everey treeFreq block

				if (((int) (roll * treeFreq)) % treeFreq == 0
						&& SkylandBiomes.getBiomeIntensity(chunkX * 16 + x, chunkZ * 16 + z) > 2) {

					int y = worldInfo.getMaxHeight()-10;
					while(limitedRegion.getType(chunkX * 16 + x,y, chunkZ * 16 + z).isAir() && y > limitedRegion.getWorld()
							.getMinHeight()) y--;

					if(limitedRegion.getType(chunkX * 16 + x,y, chunkZ * 16 + z).isSolid()) {
						limitedRegion.setType(chunkX * 16 + x,y+1, chunkZ * 16 + z, Material.OAK_LOG);
						limitedRegion.setType(chunkX * 16 + x,y+2, chunkZ * 16 + z, Material.OAK_LOG);
						limitedRegion.setType(chunkX * 16 + x,y+3, chunkZ * 16 + z, Material.OAK_LOG);
						limitedRegion.setType(chunkX * 16 + x,y+4, chunkZ * 16 + z, Material.OAK_LEAVES);
						limitedRegion.setType(chunkX * 16 + x,y+5, chunkZ * 16 + z, Material.OAK_LEAVES);
					}

				}


			}
		}





		/*
		random = new Random();
		//todo use noise map to get highest block (create method?)
		for(int iteration = 0; iteration < 10; iteration++) {
			int x = random.nextInt(16) + chunkX * 16;
			int z = random.nextInt(16) + chunkZ * 16;
			int y = 319;
			while(limitedRegion.getType(x, y, z).isAir() && y > limitedRegion.getWorld()
			.getMinHeight()) y--;

			if(limitedRegion.getType(x, y, z).isSolid()) {
				limitedRegion.setType(x, y + 1, z, Material.OAK_LOG);
				limitedRegion.setType(x, y + 2, z, Material.OAK_LOG);
				limitedRegion.setType(x, y + 3, z, Material.OAK_LOG);
				limitedRegion.setType(x, y + 4, z, Material.OAK_LEAVES);
				limitedRegion.setType(x, y + 5, z, Material.OAK_LEAVES);
			}
		}

		 */

	}

}
