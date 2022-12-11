/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi;

import org.bukkit.plugin.java.JavaPlugin;

import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.commandapi.v3.arguments.EntityOptions;

public class CommandAPIPlugin extends JavaPlugin {

	@Override
	public void onEnable() {
		EntityOptions.registerOptions();
		CommandAPI.init(this);
	}

	@Override
	public void onDisable() {
		CommandAPI.getInstance().unregisterAll();
	}
}
