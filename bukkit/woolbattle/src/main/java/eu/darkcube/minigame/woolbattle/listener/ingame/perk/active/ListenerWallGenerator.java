/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.WallGeneratorPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class ListenerWallGenerator extends BasicPerkListener {

	private static final int[][] ids =
			{{-2, -1, 0, 1, 2}, {-2, -1, 0, 1, 2}, {-2, -1, 0, 1, 2}, {-2, -1, 0, 1, 2}};

	public ListenerWallGenerator() {
		super(WallGeneratorPerk.WALL_GENERATOR);
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		new WallBlockPlacerScheduler(perk).runTaskTimer(1);
		return true;
	}

	private void deny(WBUser user, UserPerk perk) {
		Ingame.playSoundNotEnoughWool(user);
		setItem(perk);
	}

	private void setItem(UserPerk perk) {
		perk.currentPerkItem().setItem();
	}

	private class WallBlockPlacerScheduler extends Scheduler {

		private final UserPerk perk;
		private final Location center;
		private final Vector normal;
		private int id0 = 0;
		private int id1 = 0;

		public WallBlockPlacerScheduler(UserPerk perk) {
			this.perk = perk;
			center = calculateCenter();
			normal = calculateNormal();
		}

		@Override
		public void run() {
			WBUser user = perk.owner();
			Player p = user.getBukkitEntity();
			if (p.isSneaking()) {
				cancel();
				return;
			}
			if (!p.getInventory().contains(Material.WOOL, perk.perk().cost())) {
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

			setBlock(loc, perk.owner());
		}

		private Location add(Location loc, Vector vec) {
			Vector v2 = vec.clone().normalize();
			for (int i = 0; i < vec.length(); i++) {
				loc = loc.getBlock().getLocation().add(.5, 0, .5).add(v2);
			}
			return loc;
		}

		@SuppressWarnings("deprecation")
		private void setBlock(Location loc, WBUser user) {
			if (loc.getBlock().getType() == Material.AIR && !WoolBattle.getInstance()
					.getIngame().breakedWool.containsKey(loc.getBlock())) {
				payForThePerk(perk);

				loc.getBlock().setType(Material.WOOL);
				loc.getBlock().setData(user.getTeam().getType().getWoolColorByte());
				WoolBattle.getInstance().getIngame().placedBlocks.add(loc.getBlock());
			}
		}

		private Vector calculateNormal() {
			Location loc = perk.owner().getBukkitEntity().getLocation();
			loc.setPitch(0);
			return loc.getDirection().normalize();
		}

		private Location calculateCenter() {
			Location loc = perk.owner().getBukkitEntity().getLocation();
			loc.add(loc.getDirection().setY(0).normalize().multiply(2));
			return loc;
		}
	}
}
