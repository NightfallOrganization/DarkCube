/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.listener;

import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.util.Recipe;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RecipeListener implements Listener {
	private final VanillaAddons addons;

	public RecipeListener(VanillaAddons addons) {
		this.addons = addons;
	}

	@EventHandler
	public void handle(PlayerJoinEvent event) {
		Recipe.giveAll(addons, event.getPlayer());
	}
}
