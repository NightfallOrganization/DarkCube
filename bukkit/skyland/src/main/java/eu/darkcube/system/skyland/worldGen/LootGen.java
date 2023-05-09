/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import eu.darkcube.system.skyland.Equipment.Rarity;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class LootGen extends BlockPopulator {

	@Override
	public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull LimitedRegion limitedRegion) {

		random = new Random();
		if (random.nextInt(0, 10) != 1){
			return;
		}

		for(int iteration = 0; iteration < 1; iteration++) {
			int x = random.nextInt(16) + chunkX * 16;
			int z = random.nextInt(16) + chunkZ * 16;
			int y = 319;
			while(limitedRegion.getType(x, y, z).isAir() && y > limitedRegion.getWorld().getMinHeight()) y--;

			if(limitedRegion.getType(x, y, z).isSolid()) {
				limitedRegion.setType(x, y + 1, z, Material.CHEST);
				Chest chest =
						(Chest) limitedRegion.getBlockState(x, y+1, z);


				chest.getBlockInventory().setItem(1, new ItemStack(Material.STRING));
			}
		}
	}
}
