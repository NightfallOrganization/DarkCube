/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.jumpandrun;

import java.util.Random;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class JaR {

	final JaRManager manager;
	final Player player;
	JaRModule currentModule;
	JaRModule nextModule;
	int index = 0;
	final Random r = new Random();

	public JaR(JaRManager manager, Player player) {
		this.manager = manager;
		this.player = player;
	}

	public Block getCurrentBlock() {
		if (currentModule == null) {
			expand();
			index = 0;
			currentModule = nextModule;
			nextModule = null;
			if (currentModule != null)
				currentModule.build();
		}
		return currentModule.blocks[index];
	}

	public Block getNextBlock() {
		index++;
		if (currentModule == null || index == currentModule.blocks.length) {
			expand();
			index = 0;
			if (currentModule != null)
				currentModule.destroy();
			currentModule = nextModule;
			currentModule.build();
			nextModule = null;
		}
		return currentModule.blocks[index];
	}

	private void expand() {
		if (currentModule == null) {
			nextModule = new JaRModule();
			nextModule.create(this);
		} else if (nextModule == null) {
			nextModule = new JaRModule();
			nextModule.region = currentModule.region;
			nextModule.blocks[0] = currentModule.blocks[5];
		}
		if (nextModule.blocks[0] != null) {
			whl: while (true) {
				Vector dir;
				do {
					dir = new Vector(10 - r.nextInt(21), 0, 10 - r.nextInt(21));
				} while (dir.lengthSquared() == 0);
				dir.normalize();

				for (int i = 1; i < nextModule.blocks.length; i++) {
					nextModule.blocks[i] = getJump(nextModule.blocks[i - 1], dir);
					if (!nextModule.region.contains(nextModule.blocks[i])) {
						continue whl;
					}
				}
				break;
			}
		}
	}

	private Block getJump(Block start, Vector dirIn) {
		Vector dir = dirIn.clone();
		double dx = 0.5 - r.nextDouble();
		double dy = 0.1 + r.nextDouble();
		double dz = 0.5 - r.nextDouble();
		dir.add(new Vector(dx, dy, dz).multiply(1.2));
		dir.normalize();
		dir.multiply(3 + r.nextDouble() * 1.5);
		Block next = start.getLocation().add(dir).getBlock();
		return next;
	}

	public Player getPlayer() {
		return this.player;
	}
}
