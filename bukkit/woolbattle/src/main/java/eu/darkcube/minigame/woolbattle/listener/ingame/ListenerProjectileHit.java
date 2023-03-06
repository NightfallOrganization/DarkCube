/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.passive.EventEnderPearl;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import net.minecraft.server.v1_8_R3.EntityArrow;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArrow;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
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
							WoolBattle.getInstance().getIngame().setBlockDamage(b,
									WoolBattle.getInstance().getIngame().getBlockDamage(b) + 1);
						}
					}
					earrow.die();
				}
			}.runTaskLater(1);
		} else if (e.getEntityType() == EntityType.ENDER_PEARL) {
			if (e.getEntity().getShooter() instanceof Player) {
				EnderPearl pearl = (EnderPearl) e.getEntity();
				Player p = (Player) pearl.getShooter();
				Block b1 = pearl.getLocation().getBlock();
				Block b2 = pearl.getLocation().add(0, 1, 0).getBlock();
				Block b3 = pearl.getLocation().add(0, 2, 0).getBlock();
				boolean elevate = checkTPUp(b1) || checkTPUp(b2) || checkTPUp(b3);

				EventEnderPearl event = new EventEnderPearl(WBUser.getUser(p),
						b1.getType() != Material.AIR || b2.getType() != Material.AIR
								|| b3.getType() != Material.AIR, elevate);
				Bukkit.getPluginManager().callEvent(event);
				elevate = event.elevate();

				if (event.canElevate() && elevate) {
					Block block = b1;
					for (int i = 0; i < 16 && block.getType() == Material.AIR; i++) {
						block = block.getRelative(0, 1, 0);
					}
					if (block.getType() == Material.AIR) {
						return;
					}
					do {
						block = block.getRelative(0, 1, 0);
					} while (block.getType() != Material.AIR
							|| block.getRelative(0, 1, 0).getType() != Material.AIR);
					tp(p, block.getLocation().add(0.5, 0.1, 0.5));
				}
			}
		}
	}

	private void tp(Player p, Location loc) {
		new Scheduler() {
			@Override
			public void run() {
				loc.setDirection(p.getLocation().getDirection());
				p.teleport(loc);
			}
		}.runTask();
	}

	private boolean checkTPUp(Block b) {
		return b.getType() == Material.GLASS || b.getType() == Material.STAINED_GLASS;
	}
}
