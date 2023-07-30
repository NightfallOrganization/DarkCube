package eu.darkcube.system.citybuild.commands;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NoFriendlyFireHandler implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		// Wenn der Schadensverursacher und das Opfer keine Spieler sind, verhindern Sie den Schaden
		if (!(event.getDamager() instanceof Player) && !(event.getEntity() instanceof Player)) {
			event.setCancelled(true);
		}
	}
}
