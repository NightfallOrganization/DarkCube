/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.listener.ingame.perk.active;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.ingame.perk.util.BasicPerkListener;
import eu.darkcube.minigame.woolbattle.perk.perks.active.LineBuilderPerk;
import eu.darkcube.minigame.woolbattle.perk.user.UserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Line;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.Location;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ListenerLineBuilder extends BasicPerkListener {

	private static final Key DATA_SCHEDULER =
			new Key(WoolBattle.getInstance(), "linebuilderScheduler");

	public ListenerLineBuilder() {
		super(LineBuilderPerk.LINE_BUILDER);
	}

	public static Location getNiceLocation(Location loc) {
		Location l = loc.clone();
		l.setYaw(getNiceYaw(l.getYaw()));
		l.setPitch(0);
		l.setX(l.getBlockX());
		l.setY(l.getBlockY());
		l.setZ(l.getBlockZ());
		return l;
	}

	public static float getNiceYaw(float y) {
		float interval = 45f;
		float half = interval / 2f;
		y %= 360F;

		if (y < 0)
			y = 360F + y;

		for (float i = 360; i >= 0; i -= interval) {
			float bound1 = i - half;
			float bound2 = i + half;
			if (y >= bound1 && y <= bound2) {
				y = i;
				break;
			}
		}
		return y;
	}

	@Override
	protected boolean activateRight(UserPerk perk) {
		WBUser user = perk.owner();
		if (!user.user().getMetaDataStorage().has(DATA_SCHEDULER)) {
			user.user().getMetaDataStorage().set(DATA_SCHEDULER, new TheScheduler(perk));
		}
		TheScheduler s = user.user().getMetaDataStorage().get(DATA_SCHEDULER);
		s.lastLine = s.tick;
		return false;
	}

	@Override
	protected boolean activateLeft(UserPerk perk) {
		perk.owner().user().getMetaDataStorage().remove(DATA_SCHEDULER);
		return false;
	}

	private static class TheScheduler extends Scheduler {
		private int lastLine = 0;
		private Line line = null;
		private int cooldownTicks = 0;
		private UserPerk perk;
		private WBUser user;
		private int tick = 0;

		public TheScheduler(UserPerk perk) {
			this.perk = perk;
			this.user = perk.owner();
			runTaskTimer(1);
		}

		@Override
		public void run() {
			tick++;
			if (cooldownTicks > 1) {
				cooldownTicks--;
			} else {
				if (tick - lastLine > 5) {
					user.user().getMetaDataStorage().remove(DATA_SCHEDULER);
					cancel();
					return;
				}
			}
			if (tick - lastLine > 5) {
				line = null;
				return;
			}
			if (cooldownTicks > TimeUnit.SECOND.toUnit(TimeUnit.TICKS) * perk.perk().cooldown()) {
				startCooldown(perk);
				user.user().getMetaDataStorage().remove(DATA_SCHEDULER);
				cancel();
				return;
			}
			if (tick % 3 == 1) {
				payForThePerk(perk);
				cooldownTicks += TimeUnit.SECOND.toUnit(TimeUnit.TICKS);
				if (line == null) {
					line = new Line(
							getNiceLocation(user.getBukkitEntity().getLocation()).getDirection());
				}
				Location next = line.getNextBlock(user.getBukkitEntity().getLocation());
				line.addBlock(next);
				WoolBattle.getInstance().getIngame().place(user, next.getBlock());
				user.getBukkitEntity().addPotionEffect(
						new PotionEffect(PotionEffectType.SLOW, 20, 10, false, false), true);
			}
		}
	}
}
