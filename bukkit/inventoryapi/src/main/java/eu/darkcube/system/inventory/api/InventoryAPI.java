package eu.darkcube.system.inventory.api;

import eu.darkcube.system.DarkCubePlugin;

public class InventoryAPI extends DarkCubePlugin {

	private static InventoryAPI instance;

	public InventoryAPI() {
		instance = this;
	}
	
	public static InventoryAPI getInstance() {
		return instance;
	}

}
