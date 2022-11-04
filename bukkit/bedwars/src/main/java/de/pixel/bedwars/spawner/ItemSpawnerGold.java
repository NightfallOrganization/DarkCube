package de.pixel.bedwars.spawner;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import de.pixel.bedwars.shop.Cost;

public class ItemSpawnerGold extends LevellableItemSpawner {
	
	public ItemSpawnerGold() {
		this(null);
	}
	
	public ItemSpawnerGold(Block spawnerBlock) {
		this(spawnerBlock, 20 * 60);
	}

	public ItemSpawnerGold(Block spawnerBlock, int ticksDelay) {
		super(spawnerBlock, ticksDelay, 4);
		this.splitItems = false;
	}

	@Override
	public ItemStack getItem() {
		return Cost.GOLD.getItem();
	}

	@Override
	public float getTimeMultiplier() {
		return 1;
	}
}
