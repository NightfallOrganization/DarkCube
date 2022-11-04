package eu.darkcube.minigame.woolbattle.listener.endgame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerEntityDamage extends Listener<EntityDamageEvent> {

	@Override
	@EventHandler
	public void handle(EntityDamageEvent e) {
		e.setCancelled(true);
	}
}