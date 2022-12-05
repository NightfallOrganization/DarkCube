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
