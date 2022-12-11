/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
