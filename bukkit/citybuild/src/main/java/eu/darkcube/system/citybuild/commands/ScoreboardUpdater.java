/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ScoreboardUpdater extends BukkitRunnable {

	private ScoreboardHandler scoreboardHandler;

	public ScoreboardUpdater(ScoreboardHandler scoreboardHandler) {
		this.scoreboardHandler = scoreboardHandler;
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			scoreboardHandler.showPlayerLevelScoreboard(player);
		}
	}
}
