package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.StatsLink;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {

	@Override
	@EventHandler
	public void handle(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		Main main = Main.getInstance();
		Player p = e.getPlayer();
		User user = main.getUserWrapper().getUser(p.getUniqueId());
		if (user.getTeam().getType() == TeamType.SPECTATOR) {
			return;
		}
		Team t = Main.getInstance().getIngame().lastTeam.remove(user);
		if (t != null) {
			if (t.getUsers().size() != 0) {
				StatsLink.addLoss(user);
			}
		}
		Main.getInstance().sendMessage(Message.PLAYER_LEFT, user.getTeamPlayerName());
		main.getIngame().kill(user, true);
		main.getIngame().listenerGhostInteract.ghosts.remove(user);
	}

}