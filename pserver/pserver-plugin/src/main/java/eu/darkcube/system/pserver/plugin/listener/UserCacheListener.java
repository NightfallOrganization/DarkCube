package eu.darkcube.system.pserver.plugin.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.darkcube.system.pserver.plugin.user.UserCache;

public class UserCacheListener extends SingleInstanceBaseListener {

	@EventHandler(priority = EventPriority.LOWEST)
	public void handle(PlayerJoinEvent event) {
		UserCache.cache().update(event.getPlayer());
	}

}
