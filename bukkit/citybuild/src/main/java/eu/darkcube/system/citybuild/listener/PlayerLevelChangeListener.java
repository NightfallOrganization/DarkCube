/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class PlayerLevelChangeListener implements Listener {

	private final ScoreboardListener scoreboardListener;

	public PlayerLevelChangeListener(ScoreboardListener scoreboardListener) {
		this.scoreboardListener = scoreboardListener;
	}

	@EventHandler
	public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		scoreboardListener.showPlayerLevelScoreboard(player);
	}
}
