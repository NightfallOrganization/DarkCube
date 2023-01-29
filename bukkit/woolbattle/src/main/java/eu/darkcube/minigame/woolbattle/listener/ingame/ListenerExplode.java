/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerExplode extends Listener<EntityExplodeEvent> {

	@SuppressWarnings("deprecation")
	@Override
	@EventHandler
	public void handle(EntityExplodeEvent e) {
		Location mid = e.getEntity().getLocation();
		double x = mid.getX();
		double y = mid.getY();
		double z = mid.getZ();
		for (Block b : e.blockList()) {
			if (b.getType() != Material.WOOL || !WoolBattle.getInstance().getIngame().placedBlocks.contains(b)) {
				continue;
			}
			FallingBlock block = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
			double vx = block.getLocation().getX() - x;
			double vy = block.getLocation().getY() - y;
			double vz = block.getLocation().getZ() - z;
			block.setVelocity(new Vector(vx, vy, vz).multiply(.2));

			Ingame.setBlockDamage(b, 3);
		}
		e.blockList().clear();
	}
}
