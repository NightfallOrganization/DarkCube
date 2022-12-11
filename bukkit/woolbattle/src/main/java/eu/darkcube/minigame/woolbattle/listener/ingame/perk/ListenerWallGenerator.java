/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame.perk;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerWallGenerator extends BasicPerkListener {

	public ListenerWallGenerator() {
		super(PerkType.WALL_GENERATOR);
	}

	private static final int[][] ids =
			{{-2, -1, 0, 1, 2}, {-2, -1, 0, 1, 2}, {-2, -1, 0, 1, 2}, {-2, -1, 0, 1, 2}};

	@Override
	protected boolean activateRight(User user, Perk perk) {
		new WallBlockPlacerScheduler(perk).runTaskTimer(1);
		return true;
	}

	private class WallBlockPlacerScheduler extends Scheduler {

		private final Perk perk;
		private int id0 = 0;
		private int id1 = 0;
		private final Location center;
		private final Vector normal;

		public WallBlockPlacerScheduler(Perk perk) {
			this.perk = perk;
			center = calculateCenter();
			normal = calculateNormal();
		}

		@Override
		public void run() {
			User user = perk.getOwner();
			Player p = user.getBukkitEntity();
			if (p.isSneaking()) {
				cancel();
				return;
			}
			if (!p.getInventory().contains(Material.WOOL, PerkType.WALL_GENERATOR.getCost())) {
				deny(user, perk);
				cancel();
				return;
			}

			setNextBlock();

			id1++;
			if (id1 >= ids[id0].length) {
				id1 %= ids[id0].length;
				id0++;
			}

			if (id0 >= ids.length) {
				cancel();
				return;
			}
		}

		private void setNextBlock() {
			Location loc = center.clone();
			Location loc2 = loc.clone();
			loc2.setDirection(normal.clone()).setYaw(loc2.getYaw() + 90);
			Vector vec = loc2.getDirection().normalize();
			loc = add(loc, vec.multiply(ids[id0][id1]));
			loc.add(0, id0 - 1, 0);

			setBlock(loc, perk.getOwner());
		}

		private Location add(Location loc, Vector vec) {
			Vector v2 = vec.clone().normalize();
			for (int i = 0; i < vec.length(); i++) {
				loc = loc.getBlock().getLocation().add(.5, 0, .5).add(v2);
			}
			return loc;
		}

		@SuppressWarnings("deprecation")
		private void setBlock(Location loc, User user) {
			if (loc.getBlock().getType() == Material.AIR
					&& !WoolBattle.getInstance().getIngame().breakedWool
							.containsKey(loc.getBlock())) {

				ItemManager.removeItems(perk.getOwner(),
						perk.getOwner().getBukkitEntity().getInventory(), user.getSingleWoolItem(),
						PerkType.WALL_GENERATOR.getCost());

				loc.getBlock().setType(Material.WOOL);
				loc.getBlock().setData(user.getTeam().getType().getWoolColorByte());
				WoolBattle.getInstance().getIngame().placedBlocks.add(loc.getBlock());
			}
		}

		private Vector calculateNormal() {
			Location loc = perk.getOwner().getBukkitEntity().getLocation();
			loc.setPitch(0);
			return loc.getDirection().normalize();
		}

		private Location calculateCenter() {
			Location loc = perk.getOwner().getBukkitEntity().getLocation();
			loc.add(loc.getDirection().setY(0).normalize().multiply(2));
			return loc;
		}
	}

	private void deny(User user, Perk perk) {
		Ingame.playSoundNotEnoughWool(user);
		setItem(perk);
	}

	private void setItem(Perk perk) {
		perk.getOwner().getBukkitEntity().setItemInHand(perk.calculateItem());
	}
}
