/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.gamephase.end;

import org.bukkit.Bukkit;
import org.bukkit.World;

import eu.darkcube.system.miners.util.Timer;

public class EndPhase {

	private Timer endTimer;

	public EndPhase() {
		endTimer = new Timer() {

			@Override
			public void onIncrement() {
				updateXpBar();
			}

			@Override
			public void onEnd() {
				Bukkit.shutdown();
			}
		};
	}

	public void enable() {
		endTimer.start(10000);
		World world = Bukkit.getWorld("world");
		Bukkit.getOnlinePlayers().forEach(p -> p.teleport(world.getSpawnLocation()));
	}

	public void updateXpBar() {
		float totalTime = endTimer.getOriginalEndTime() - endTimer.getStartTime();
		float div = endTimer.getTimeRemainingMillis() / totalTime;

		int remainingSeconds = (int) Math.ceil(endTimer.getTimeRemainingMillis() / 1000);

		Bukkit.getOnlinePlayers().forEach(p -> {
			p.setExp(div);
			p.setLevel(remainingSeconds);
		});
	}

}
