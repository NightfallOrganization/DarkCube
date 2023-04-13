/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.perk.perks.passive;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.event.perk.other.DoubleJumpEvent;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.Perk.Cooldown.Unit;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.user.DefaultUserPerk;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;
import eu.darkcube.system.util.data.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;

public class StomperPerk extends Perk {
	public static final PerkName STOMPER = new PerkName("STOMPER");
	private static final Key active = new Key(WoolBattle.instance(), "perk_stomper_active");

	public StomperPerk() {
		super(ActivationType.PASSIVE, STOMPER, new Cooldown(Unit.ACTIVATIONS, 0), 10,
				Item.PERK_STOMPER, DefaultUserPerk::new);
		addListener(new StomperListener());
		addScheduler(new StomperScheduler());
	}

	private static class ShockwaveScheduler extends Scheduler {
		private static final double speedBlocksPerTick = 1;
		private static final double particlesPerBlock = 2;
		private final WBUser user;
		private final double radius;
		private final Collection<WBUser> hit = new ArrayList<>(0);
		private double cur = 0;

		public ShockwaveScheduler(WBUser user, double radius) {
			this.user = user;
			this.radius = radius;
		}

		@Override
		public void run() {
			cur += speedBlocksPerTick;
			double circumference = Math.PI * 2 * cur;
			
		}
	}

	public class StomperScheduler extends Scheduler implements ConfiguredScheduler {

		@Override
		public void run() {
			for (WBUser user : WBUser.onlineUsers()) {
				if (!user.getTeam().canPlay())
					continue;
				if (!user.user().getMetaDataStorage().has(active))
					continue;
				if (!user.getBukkitEntity().isOnGround())
					continue;
				int removed;
				if ((removed = user.removeWool(cost())) != cost()) {
					user.addWool(removed);
					continue;
				}
				int size = user.user().getMetaDataStorage().remove(active);
				double rad = size * 5;
				new ShockwaveScheduler(user, rad).runTaskTimer(1);
			}
		}

		@Override
		public void start() {
			runTaskTimer(1);
		}

		@Override
		public void stop() {
			cancel();
		}
	}

	public class StomperListener implements Listener {
		@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
		public void handle(DoubleJumpEvent event) {
			int size = event.user().user().getMetaDataStorage().getOr(active, 0);
			size += event.user().perks().perks(perkName()).size();
			if (size > 0) {
				event.user().user().getMetaDataStorage().set(active, size);
			}
		}
	}
}
