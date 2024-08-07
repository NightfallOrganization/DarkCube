/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.bukkit.structure.StructureManager;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class StructurePopulator extends BlockPopulator {
	@Override
	public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX,
			int chunkZ, @NotNull LimitedRegion limitedRegion) {

		StructureManager structureManager = Skyland.getInstance().getServer().getStructureManager();
		structureManager.loadStructure(new NamespacedKey(Skyland.getInstance(), "test"));
		structureManager.getStructure(new NamespacedKey(Skyland.getInstance(), "test"))
				.place(new Location(limitedRegion.getWorld(), chunkX* 16, 100, chunkZ*16), true, StructureRotation.NONE, Mirror.NONE, -1, 1, new Random());


	}
}
