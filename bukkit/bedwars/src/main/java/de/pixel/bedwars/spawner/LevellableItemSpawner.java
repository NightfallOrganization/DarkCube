/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.spawner;

import org.bukkit.block.Block;

public abstract class LevellableItemSpawner extends ItemSpawner {

	private int ticksDelay;
	private int level;

	public LevellableItemSpawner(Block spawnerBlock, int ticksDelay, int maxItemCount) {
		super(spawnerBlock, maxItemCount);
		this.ticksDelay = ticksDelay;
	}
	
	public final int getBaseTicksDelay() {
		return ticksDelay;
	}

	@Override
	public final int getTicksDelay() {
		return Math.round(getTimeMultiplier() * getBaseTicksDelay());
	}
	
	public final void setLevel(int level) {
		this.level = level;
		start();
	}

	public final int getLevel() {
		return level;
	}

	public abstract float getTimeMultiplier();
}
