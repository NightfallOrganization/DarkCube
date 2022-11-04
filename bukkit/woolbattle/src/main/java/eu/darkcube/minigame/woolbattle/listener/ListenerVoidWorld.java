package eu.darkcube.minigame.woolbattle.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.world.WorldInitEvent;

import eu.darkcube.minigame.woolbattle.Main;

public class ListenerVoidWorld extends Listener<WorldInitEvent> {

	@Override
	@EventHandler
	public void handle(WorldInitEvent e) {
		Main.getInstance().loadWorld(e.getWorld());
	}
}
