/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.spawner;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.shop.Cost;

public class ItemSpawnerBronze extends LevellableItemSpawner {
	
	public ItemSpawnerBronze() {
		this(null);
	}
	
	public ItemSpawnerBronze(Block spawnerBlock) {
		this(spawnerBlock, 10);
	}

	public ItemSpawnerBronze(Block spawnerBlock, int ticksDelay) {
		super(spawnerBlock, ticksDelay, 128);
	}

	@Override
	public ItemStack getItem() {
		return Cost.BRONZE.getItem();
	}

	@Override
	public float getTimeMultiplier() {
		return 1;
	}
}
