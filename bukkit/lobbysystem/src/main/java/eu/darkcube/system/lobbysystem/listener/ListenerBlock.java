package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;

public class ListenerBlock extends BaseListener {

	@EventHandler
	public void handle(BlockPlaceEvent e) {
		User user = UserWrapper.getUser(e.getPlayer().getUniqueId());
		if(!user.isBuildMode()) {
			e.setBuild(false);
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void handle(BlockBreakEvent e) {
		User user = UserWrapper.getUser(e.getPlayer().getUniqueId());
		e.setExpToDrop(0);	
		if(!user.isBuildMode()) {
			e.setCancelled(true);
		}
	}
}