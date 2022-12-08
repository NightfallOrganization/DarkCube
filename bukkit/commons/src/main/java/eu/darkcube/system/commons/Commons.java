/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commons;

import eu.darkcube.system.DarkCubePlugin;

public class Commons extends DarkCubePlugin {

	@Override
	public void onEnable() {
		AsyncExecutor.start();
	}
	
	@Override
	public void onDisable() {
		AsyncExecutor.stop();
	}
}
