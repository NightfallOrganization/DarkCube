package eu.darkcube.system.knockout;

import eu.darkcube.system.DarkCubePlugin;

public class KnockOut extends DarkCubePlugin {

	private static KnockOut instance;
	
	public KnockOut() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		
	}
	
	public static KnockOut getInstance() {
		return instance;
	}
}
