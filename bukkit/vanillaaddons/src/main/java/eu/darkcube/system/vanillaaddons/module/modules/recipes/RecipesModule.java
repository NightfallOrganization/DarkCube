/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.recipes;

import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RecipesModule implements Module, Listener {
	private final VanillaAddons addons;

	public RecipesModule(VanillaAddons addons) {
		this.addons = addons;
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, addons);
		new BukkitRunnable() {
			@Override
			public void run() {
				if (Recipe.save) {
					Recipe.save = false;
					Recipe.saveDataVersions0();
				}
			}
		}.runTaskTimer(addons, 1, 1);
	}

	@EventHandler
	public void handle(PlayerJoinEvent event) {
		Recipe.giveAll(addons, event.getPlayer());
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
}
