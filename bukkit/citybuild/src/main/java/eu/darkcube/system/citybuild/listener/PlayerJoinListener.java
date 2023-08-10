/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

	private ScoreboardListener scoreboardListener;
	private HashMap<UUID, Long> joinTimes;

	public PlayerJoinListener(ScoreboardListener scoreboardListener) {
		this.scoreboardListener = scoreboardListener;
		joinTimes = new HashMap<>();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		scoreboardListener.showPlayerLevelScoreboard(player);
		joinTimes.put(player.getUniqueId(), System.currentTimeMillis());

		// Erzwinge Ressourcenpaket-Download
		String texturePackUrl = "";
		player.setResourcePack(texturePackUrl);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		joinTimes.remove(player.getUniqueId());
	}
//	@EventHandler
//	public void handle(ReloadSinglePrefixEvent event) {
//		event.setNewPrefix("Test");
//	}
}
