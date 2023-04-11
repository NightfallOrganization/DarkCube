/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.loommechanics;

import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class LoomMechanics implements Module, Listener {
	private final VanillaAddons addons;

	public LoomMechanics(VanillaAddons addons) {
		this.addons = addons;
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, addons);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
}
