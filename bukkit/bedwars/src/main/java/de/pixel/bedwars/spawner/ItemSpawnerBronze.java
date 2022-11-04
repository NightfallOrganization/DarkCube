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
