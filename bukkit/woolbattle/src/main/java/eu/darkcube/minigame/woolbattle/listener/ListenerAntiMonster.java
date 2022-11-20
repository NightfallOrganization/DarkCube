package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class ListenerAntiMonster implements Listener {

	@EventHandler
	public void handle(EntitySpawnEvent event) {
		if (event.getEntity() instanceof Monster) {
			event.setCancelled(true);
		}
	}

}
