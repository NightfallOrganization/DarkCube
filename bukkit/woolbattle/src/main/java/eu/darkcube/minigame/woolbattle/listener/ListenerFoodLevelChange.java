package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class ListenerFoodLevelChange extends Listener<FoodLevelChangeEvent> {
	@Override
	@EventHandler
	public void handle(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
}