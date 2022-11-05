package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

public class ListenerDamage extends BaseListener {

	@EventHandler
	public void handle(EntityDamageEvent e) {
		e.setCancelled(true);
	}
}
