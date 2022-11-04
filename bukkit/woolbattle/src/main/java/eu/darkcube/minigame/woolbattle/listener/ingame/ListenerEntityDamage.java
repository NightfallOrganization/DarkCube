package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.user.User;

public class ListenerEntityDamage extends Listener<EntityDamageEvent> {
	@Override
	@EventHandler
	public void handle(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			User user = Main.getInstance().getUserWrapper().getUser(p.getUniqueId());
			Ingame ingame = Main.getInstance().getIngame();
			if (ingame.isGlobalSpawnProtection || user.hasSpawnProtection()) {
				e.setCancelled(true);
				return;
			}
			e.setDamage(0);
			switch (e.getCause()) {
			case FALL:
			case STARVATION:
				e.setCancelled(true);
				break;
			case SUFFOCATION:
				if (Main.getInstance().isEpGlitch()) {
					int ticks = user.getTicksAfterLastHit();
					if (ticks < 200) {
						ticks += 60;
						user.setTicksAfterLastHit(ticks);
					}
					break;
				}
				e.setCancelled(true);
				break;
			default:
				break;
			}
		}
	}
}