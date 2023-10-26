/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen.structures;

import org.bukkit.block.BlockState;
import org.bukkit.structure.Palette;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomPallette implements Palette {

	List<BlockState> blocks = new ArrayList<>();

	public CustomPallette(){

	}

	public CustomPallette(List<BlockState> blocks){
		this.blocks = blocks;
	}

	@Override
	public @NotNull List<BlockState> getBlocks() {
		return blocks;
	}

	@Override
	public int getBlockCount() {
		return blocks.size();
	}
}
