package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntitySpawnEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerEntitySpawn extends Listener<EntitySpawnEvent> {

	@Override
	@EventHandler
	public void handle(EntitySpawnEvent e) {
		if(e.getEntityType() == EntityType.CHICKEN) {
			e.setCancelled(true);
		}
	}
}
