/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.game.ingame;

import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler.ConfiguredScheduler;

public class SchedulerTick extends Scheduler implements ConfiguredScheduler {

	@Override
	public void run() {
		for (WBUser user : WBUser.onlineUsers()) {
			if (user.getTeam().getType() != TeamType.SPECTATOR) {
				if (user.getTicksAfterLastHit() < 1200)
					user.setTicksAfterLastHit(user.getTicksAfterLastHit() + 1);
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
