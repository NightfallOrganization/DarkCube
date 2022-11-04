package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerDeathMove extends Listener<PlayerMoveEvent> {

	private Main main = Main.getInstance();

	@Override
	@EventHandler
	public void handle(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		User user = main.getUserWrapper().getUser(p.getUniqueId());
		if (user.getTeam().getType() != TeamType.SPECTATOR) {
			if (p.getLocation().getY() <= main.getMap().getDeathHeight()) {
				if (main.getIngame().listenerGhostInteract.ghosts.containsKey(user)) {
					main.getIngame().listenerGhostEntityDamageByEntity.reset(user,
							main.getIngame().listenerGhostInteract.ghosts.get(user));
					return;
				}
				if (user.getTicksAfterLastHit() <= 200) {
					main.getIngame().kill(user);
					user.setTicksAfterLastHit(201);
				} else {
					p.teleport(user.getTeam().getSpawn());
				}
			}
			return;
		}
		if (e.getTo().getY() < 0) {
			p.teleport(user.getTeam().getSpawn());
		}
//		Main main = Main.getInstance();
//
//		@Override
//		public void run() {
//			for (User user : main.getUserWrapper().getUsers()) {
//				if (user.getTeam().getType() != TeamType.SPECTATOR) {
//					if (user.getBukkitEntity().getLocation().getBlockY() <= main.getMap().getDeathHeight()) {
//						if (listenerGhostInteract.ghosts.containsKey(user)) {
//							listenerGhostEntityDamageByEntity.reset(user, listenerGhostInteract.ghosts.get(user));
//							continue;
//						}
//						if (user.getTicksAfterLastHit() <= 200) {
//							kill(user);
//							user.setTicksAfterLastHit(201);
//						} else {
//							user.getBukkitEntity().teleport(user.getTeam().getSpawn());
//						}
//					}
//				}
//			}
//		}		
	}

}
