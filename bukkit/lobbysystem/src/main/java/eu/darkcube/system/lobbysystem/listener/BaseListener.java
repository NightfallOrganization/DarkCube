package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import eu.darkcube.system.lobbysystem.Lobby;

public class BaseListener implements Listener {

	public BaseListener() {
		Lobby.getInstance().getServer().getPluginManager().registerEvents(this,
				Lobby.getInstance());
	}

	public void unregister() {
		HandlerList.unregisterAll(this);
	}
}
