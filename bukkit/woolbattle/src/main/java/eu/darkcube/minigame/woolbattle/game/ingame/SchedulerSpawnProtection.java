/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game.ingame;

import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ParticleEffect;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;

import java.util.stream.Collectors;

public class SchedulerSpawnProtection extends Scheduler implements ConfiguredScheduler {
	private final Ingame ingame;
	private int protectionTicks = Ingame.SPAWNPROTECTION_TICKS_GLOBAL;

	public SchedulerSpawnProtection(Ingame ingame) {
		this.ingame = ingame;
	}

	@Override
	public void run() {
		if (protectionTicks == 1) {
			protectionTicks--;
			ingame.isGlobalSpawnProtection = false;
			for (WBUser user : WBUser.onlineUsers()) {
				user.getBukkitEntity().setExp(0);
			}
		}
		if (protectionTicks > 1) {
			ingame.isGlobalSpawnProtection = true;
			this.protectionTicks--;
			for (WBUser user : WBUser.onlineUsers()) {
				user.getBukkitEntity().setExp((float) this.protectionTicks
						/ (float) Ingame.SPAWNPROTECTION_TICKS_GLOBAL);
			}
		} else {
			for (WBUser user : WBUser.onlineUsers()) {
				if (user.getSpawnProtectionTicks() > 0) {
					user.setSpawnProtectionTicks(user.getSpawnProtectionTicks() - 1);
				}
				if (user.projectileImmunityTicks() > 0) {
					user.projectileImmunityTicks(user.projectileImmunityTicks() - 1);
					ParticleEffect.VILLAGER_HAPPY.display(0.3F, 1F, 0.3F, 1, 2,
							user.getBukkitEntity().getLocation(),
							WBUser.onlineUsers().stream().filter(WBUser::particles)
									.map(WBUser::getBukkitEntity).collect(Collectors.toList()));
				}
			}
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
