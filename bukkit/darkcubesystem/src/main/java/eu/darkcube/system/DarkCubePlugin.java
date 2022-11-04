package eu.darkcube.system;

import org.bukkit.event.Listener;

public class DarkCubePlugin extends Plugin implements Listener {
	
	@Override
	public String getCommandPrefix() {
		return "§5Dark§dCube";
	}
}