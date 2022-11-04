package eu.darkcube.minigame.smash.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class IngameEntityDamage extends BaseListener {

	@EventHandler
	public void handle(EntityDamageEvent e) {
		if(e.getCause() == DamageCause.FALL) {
			e.setCancelled(true);
		}
	}
}
