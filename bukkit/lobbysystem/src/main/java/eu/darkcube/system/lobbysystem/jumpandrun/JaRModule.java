/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class JaRModule {

	private static final int length = 5;
	JaRRegion region;
	final Block[] blocks = new Block[length];

	public void create(JaR jar) {
		if (jar.manager.regions.size() == 0) {
			jar.player.sendMessage("§cKeine Jump And Run Regions festgelegt!");
			return;
		}
		region = jar.manager.regions.get(jar.r.nextInt(jar.manager.regions.size()));
		int startY = region.y;
		int startX = region.x + jar.r.nextInt(region.widthX);
		int startZ = region.z + jar.r.nextInt(region.widthZ);
		Block start = region.world.getBlockAt(startX, startY, startZ);
		blocks[0] = start;
	}

	public void build() {
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i] != null)
				blocks[i].setType(Material.STAINED_CLAY);
		}
	}

	public void destroy() {
		for (int i = 0; i < blocks.length; i++) {
			if (blocks[i] != null)
				blocks[i].setType(Material.AIR);
		}
	}

}
