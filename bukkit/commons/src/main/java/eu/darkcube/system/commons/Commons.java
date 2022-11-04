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
