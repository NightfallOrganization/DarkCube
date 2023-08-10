/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.citybuild.scheduler;

import eu.darkcube.system.citybuild.listener.ScoreboardListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdater extends BukkitRunnable {

	private ScoreboardListener scoreboardListener;

	public ScoreboardUpdater(ScoreboardListener scoreboardListener) {
		this.scoreboardListener = scoreboardListener;
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			scoreboardListener.showPlayerLevelScoreboard(player);
		}
	}
}
