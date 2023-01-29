/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class Line {

	private Location[] blocks = new Location[0];
	private Vector direction;

	public Line(Vector direction) {
		this.direction = direction.clone().normalize();
	}

	private Location getLocation(Location o) {
		o = o.clone();
		o.setX(o.getBlockX() + .5);
		o.setY(o.getBlockY() + .5);
		o.setZ(o.getBlockZ() + .5);
		return o;
	}

	public void addBlock(Block block) {
		addBlock(block.getLocation());
	}

	public void addBlock(Location block) {
		blocks = Arrays.addAfter(blocks, getLocation(block));
	}

	public Location getNextBlock() {
		if (blocks.length == 0)
			return null;
		return getLastBlock().add(direction);
	}

	public Location getNextBlock(Location fallbackLastBlock) {
		if (blocks.length == 0) {
			return getLocation(fallbackLastBlock).add(direction);
		}
		return getNextBlock();
	}

	public Location getLastBlock() {
		if (blocks.length <= 1) {
			return getFirstBlock();
		}
		return blocks[blocks.length - 1].clone();
	}

	public Location getFirstBlock() {
		if (blocks.length == 0)
			return null;
		return blocks[0].clone();
	}

}
