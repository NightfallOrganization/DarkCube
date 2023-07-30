package eu.darkcube.system.citybuild.commands;

import de.dasbabypixel.prefixplugin.PrefixPlugin;
import de.dasbabypixel.prefixplugin.ReloadSinglePrefixEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

	private ScoreboardHandler scoreboardHandler;
	private HashMap<UUID, Long> joinTimes;

	public PlayerJoinListener(ScoreboardHandler scoreboardHandler) {
		this.scoreboardHandler = scoreboardHandler;
		this.joinTimes = joinTimes;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		scoreboardHandler.showPlayerLevelScoreboard(player);
		joinTimes.put(player.getUniqueId(), System.currentTimeMillis());

		// Erzwinge Ressourcenpaket-Download
		String texturePackUrl = "https://cdn.discordapp.com/attachments/834274370670690315/1134951154795696170/pack.zip"; // Ersetzen Sie diesen Link durch den Link zu Ihrem Ressourcenpaket
		player.setResourcePack(texturePackUrl);
	}

//	@EventHandler
//	public void handle(ReloadSinglePrefixEvent event) {
//		event.setNewPrefix("Test");
//	}
}
