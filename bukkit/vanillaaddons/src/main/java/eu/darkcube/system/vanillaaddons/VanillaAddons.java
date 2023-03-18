/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.userapi.data.UserModifier;
import eu.darkcube.system.vanillaaddons.AUser.Modifier;
import eu.darkcube.system.vanillaaddons.inventory.InventoryRegistry;
import eu.darkcube.system.vanillaaddons.listener.*;
import eu.darkcube.system.vanillaaddons.util.Recipe;
import eu.darkcube.system.vanillaaddons.util.Teleporter;
import eu.darkcube.system.vanillaaddons.util.Teleporters;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VanillaAddons extends DarkCubePlugin {
	private Map<World, Teleporters> teleporters = new HashMap<>();
	private InventoryRegistry inventoryRegistry;
	private UserModifier userModifier;
	private static VanillaAddons instance = null;

	public VanillaAddons() {
		super("vanillaaddons");
		instance = this;
	}

	public static VanillaAddons instance() {
		return instance;
	}

	@Override
	public void onDisable() {
		for (World world : Bukkit.getWorlds()) {
			TeleporterListener.saveTeleporters(this, world);
		}
		UserAPI.getInstance().removeModifier(userModifier);
		Recipe.unregisterAll(this);
	}

	@Override
	public void onEnable() {
		inventoryRegistry = new InventoryRegistry();
		Recipe.registerAll(this);
		UserAPI.getInstance().addModifier(userModifier = new Modifier(this));
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InventoryListener(this), this);
		pm.registerEvents(new CreeperListener(), this);
		pm.registerEvents(new FarmlandListener(), this);
		pm.registerEvents(new TeleporterListener(this), this);
		pm.registerEvents(new RecipeListener(this), this);
		for (World world : Bukkit.getWorlds()) {
			TeleporterListener.loadTeleporters(this, world);
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			Recipe.giveAll(this, player);
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for (World world : Bukkit.getWorlds()) {
					TeleporterListener.saveTeleporters(VanillaAddons.this, world);
				}
			}
		}.runTaskTimer(this, 30 * 20, 30 * 20);
	}

	public List<Teleporter> teleporters(World world) {
		return teleporters.computeIfAbsent(world, k -> new Teleporters()).teleporters;
	}

	public Map<World, Teleporters> teleporters() {
		return teleporters;
	}

	public InventoryRegistry inventoryRegistry() {
		return inventoryRegistry;
	}
}
