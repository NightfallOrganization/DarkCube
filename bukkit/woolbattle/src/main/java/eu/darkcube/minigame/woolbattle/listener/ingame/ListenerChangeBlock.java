package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerChangeBlock extends Listener<EntityChangeBlockEvent> {
	@Override
	@EventHandler
	public void handle(EntityChangeBlockEvent e) {
		if (e.getEntityType() == EntityType.FALLING_BLOCK) {
			if (e.getTo() == Material.WOOL) {
				WoolBattle.getInstance().getIngame().placedBlocks.add(e.getBlock());
			} else {
				e.setCancelled(true);
			}
		}
	}
}