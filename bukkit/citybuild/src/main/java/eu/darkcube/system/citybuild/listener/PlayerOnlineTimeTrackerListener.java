/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import eu.darkcube.system.citybuild.listener.ScoreboardListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerOnlineTimeTrackerListener implements Listener {

	private final ScoreboardListener scoreboardListener;
	private final Map<UUID, Long> joinTimes;
	private final Map<UUID, Long> totalOnlineTimes;  // Speichert die Gesamtspielzeit

	public PlayerOnlineTimeTrackerListener(ScoreboardListener scoreboardListener) {
		this.scoreboardListener = scoreboardListener;
		this.joinTimes = new HashMap<>();
		this.totalOnlineTimes = new HashMap<>();  // Initialisiert die Map
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		joinTimes.put(player.getUniqueId(), System.currentTimeMillis());

		// Lade die gespeicherte Spielzeit
		long savedOnlineTime = totalOnlineTimes.getOrDefault(player.getUniqueId(), 0L);
		System.out.println(player.getName() + " hatte eine gespeicherte Spielzeit von " + savedOnlineTime + " Minuten");
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		long joinTime = joinTimes.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
		long onlineTime = System.currentTimeMillis() - joinTime;

		// Convertiere onlineTime zu Minuten
		long onlineTimeInMinutes = (onlineTime / 1000) / 60;

		// Addiere die aktuelle Spielzeit zur gespeicherten Spielzeit und speichere das Ergebnis
		long totalOnlineTime = totalOnlineTimes.getOrDefault(player.getUniqueId(), 0L) + onlineTimeInMinutes;
		totalOnlineTimes.put(player.getUniqueId(), totalOnlineTime);

		System.out.println(player.getName() + " hat eine Gesamtspielzeit von " + totalOnlineTime + " Minuten");

		joinTimes.remove(player.getUniqueId());
	}

	public long getOnlineTime(Player player) {
		long joinTime = joinTimes.getOrDefault(player.getUniqueId(), System.currentTimeMillis());
		long onlineTimeInMilliseconds = System.currentTimeMillis() - joinTime;
		long onlineTimeInMinutes = onlineTimeInMilliseconds / 1000 / 60;

		// Addiere die gespeicherte Spielzeit zur aktuellen Spielzeit
		onlineTimeInMinutes += totalOnlineTimes.getOrDefault(player.getUniqueId(), 0L);

		return onlineTimeInMinutes;
	}
}
