/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class NoFriendlyFireListener implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		// Wenn der Schadensverursacher und das Opfer keine Spieler sind, verhindern Sie den Schaden
		if (!(event.getDamager() instanceof Player) && !(event.getEntity() instanceof Player)) {
			event.setCancelled(true);
		}
	}
}
