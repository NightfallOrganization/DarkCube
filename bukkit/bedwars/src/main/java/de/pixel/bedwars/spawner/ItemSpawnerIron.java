package de.pixel.bedwars.spawner;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.shop.Cost;

public class ItemSpawnerIron extends LevellableItemSpawner {
	
	public ItemSpawnerIron() {
		this(null);
	}
	
	public ItemSpawnerIron(Block spawnerBlock) {
		this(spawnerBlock, 20 * 15);
	}

	public ItemSpawnerIron(Block spawnerBlock, int ticksDelay) {
		super(spawnerBlock, ticksDelay, 16);
		this.splitItems = false;
	}

	@Override
	public ItemStack getItem() {
		return Cost.IRON.getItem();
	}

	@Override
	public float getTimeMultiplier() {
		return 1;
	}
}