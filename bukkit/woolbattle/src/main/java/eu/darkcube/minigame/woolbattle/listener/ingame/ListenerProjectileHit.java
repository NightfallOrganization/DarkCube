/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;

public class ListenerProjectileHit extends Listener<ProjectileHitEvent> {

	@Override
	@EventHandler
	public void handle(ProjectileHitEvent e) {
		if (e.getEntityType() == EntityType.ARROW) {
			Arrow arrow = (Arrow) e.getEntity();
			if (!(arrow.getShooter() instanceof Player)) {
				return;
			}

			new Scheduler() {
				@Override
				public void run() {
					EntityArrow earrow = ((CraftArrow) e.getEntity()).getHandle();
					NBTTagCompound nbt = new NBTTagCompound();
					earrow.b(nbt);
					double x = nbt.getShort("xTile");
					double y = nbt.getShort("yTile");
					double z = nbt.getShort("zTile");
					if (y != -1) {
						Block b = e.getEntity().getWorld().getBlockAt((int) x, (int) y, (int) z);
						if (b.getType() == Material.WOOL) {
							WoolBattle.instance().getIngame().setBlockDamage(b,
									WoolBattle.instance().getIngame().getBlockDamage(b) + 1);
						}
					}
					earrow.die();
				}
			}.runTaskLater(1);
		}
	}
}
