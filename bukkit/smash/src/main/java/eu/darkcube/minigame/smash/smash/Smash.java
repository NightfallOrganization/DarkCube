/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.smash;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.scheduler.BukkitRunnable;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.api.user.User;

public abstract class Smash {

	private static Map<User, Integer> delays = new HashMap<>();
	private final int delayTicks;

	public Smash(int delayTicks) {
		this.delayTicks = delayTicks;
	}

	public final void execute(User user) {
		if (!(this instanceof ShieldSmash) && (user.isShieldActive() || user.getStunTicks() != 0)) {
			return;
		}
		if (delays.containsKey(user)) {
			return;
		}
		delays.put(user, delayTicks);
		new BukkitRunnable() {
			private int delayTicks = Smash.this.delayTicks;

			@Override
			public void run() {
				delayTicks--;
				delays.put(user, delayTicks);
				user.getPlayer().setExp((float) delayTicks / (float) Smash.this.delayTicks);
				if (delayTicks <= 0) {
					delays.remove(user);
					cancel();
				}
			}
		}.runTaskTimer(Main.getInstance(), 1, 1);
		execute0(user);
	}

	protected abstract void execute0(User user);
}
