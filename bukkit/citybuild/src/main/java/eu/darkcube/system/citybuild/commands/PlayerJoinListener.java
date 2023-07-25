package eu.darkcube.system.citybuild.commands;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

	private final ScoreboardHandler scoreboardHandler;

	public PlayerJoinListener(ScoreboardHandler scoreboardHandler) {
		this.scoreboardHandler = scoreboardHandler;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		scoreboardHandler.showPlayerLevelScoreboard(event.getPlayer());
	}
}
