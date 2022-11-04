package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerEntityDamage extends Listener<EntityDamageEvent> {

	@Override
	@EventHandler
	public void handle(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player)
			e.setCancelled(true);
	}
}