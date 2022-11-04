package eu.darkcube.minigame.smash.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class LobbyEntityDamage extends BaseListener {

	@EventHandler
	public void handle(EntityDamageEvent e) {
		e.setCancelled(true);
	}
}
