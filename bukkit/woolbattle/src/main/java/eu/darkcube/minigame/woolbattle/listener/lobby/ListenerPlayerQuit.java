package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Lobby;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {

	@Override
	@EventHandler
	public void handle(PlayerQuitEvent e) {
		User user = Main.getInstance().getUserWrapper().getUser(e.getPlayer().getUniqueId());
		Lobby lobby = Main.getInstance().getLobby();
		lobby.getScoreboardByUser().remove(user);
		lobby.VOTES_MAP.remove(user);
		lobby.VOTES_EP_GLITCH.remove(user);
		lobby.VOTES_LIFES.remove(user);
		Main.getInstance().getLobby().recalculateMap();
		Main.getInstance().getLobby().recalculateEpGlitch();

		for (User t : Main.getInstance().getUserWrapper().getUsers()) {
			t.getBukkitEntity().sendMessage(Message.PLAYER_LEFT.getMessage(t, user.getTeamPlayerName()));
		}
		Main.getInstance().sendConsole(Message.PLAYER_LEFT.getServerMessage(user.getTeamPlayerName()));

		Main.getInstance().getUserWrapper().getUsers().forEach(t -> Main.getInstance().setOnline(t));
		e.setQuitMessage(null);
	}
}
