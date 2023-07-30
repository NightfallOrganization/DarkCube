package eu.darkcube.system.citybuild.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;

public class PlayerLevelChangeListener implements Listener {

	private final ScoreboardHandler scoreboardHandler;

	public PlayerLevelChangeListener(ScoreboardHandler scoreboardHandler) {
		this.scoreboardHandler = scoreboardHandler;
	}

	@EventHandler
	public void onPlayerLevelChange(PlayerLevelChangeEvent event) {
		Player player = event.getPlayer();
		scoreboardHandler.showPlayerLevelScoreboard(player);
	}
}
