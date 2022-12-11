/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
