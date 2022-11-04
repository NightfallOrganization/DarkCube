package de.pixel.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class LobbyFoodLevelChange implements Listener {

	@EventHandler
	public void handle(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
}
