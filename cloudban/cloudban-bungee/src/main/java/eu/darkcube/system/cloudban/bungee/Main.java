package eu.darkcube.system.cloudban.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	private static Main instance;
	
	public Main() {
		instance = this;
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	@Override
	public void onEnable() {
//		BungeeCommandWrapper.create();
	}
}
