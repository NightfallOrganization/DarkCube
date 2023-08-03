/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game.ingame;

import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public class SchedulerResetWool extends Scheduler implements ConfiguredScheduler {
	private final Map<Block, DyeColor> brokenWool;
	private final Collection<Block> userPlacedBlocks;

	public SchedulerResetWool(Map<Block, DyeColor> brokenWool, Collection<Block> userPlacedBlocks) {
		this.brokenWool = brokenWool;
		this.userPlacedBlocks = userPlacedBlocks;
	}

	@Override
	public synchronized void run() {
		for (Entry<Block, DyeColor> e : brokenWool.entrySet()) {
			BlockState state = e.getKey().getState();
			state.setType(Material.WOOL);
			Wool wool = (Wool) state.getData();
			wool.setColor(e.getValue());
			state.setData(wool);
			state.update(true);
			userPlacedBlocks.remove(e.getKey());
		}
		brokenWool.clear();
	}

	@Override
	public void start() {
		runTaskTimer(16);
	}

	@Override
	public void stop() {
		cancel();
	}
}
