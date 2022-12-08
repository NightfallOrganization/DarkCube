/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.DarkCubePlugin;

public class PluginUserAPI extends DarkCubePlugin {
	private static PluginUserAPI instance;

	public PluginUserAPI() {
		instance = this;
	}

	public static PluginUserAPI getInstance() {
		return instance;
	}
}
