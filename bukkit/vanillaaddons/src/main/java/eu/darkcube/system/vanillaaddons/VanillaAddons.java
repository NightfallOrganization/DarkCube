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
import eu.darkcube.system.vanillaaddons.listener.ArmorListener;
import eu.darkcube.system.vanillaaddons.listener.InventoryListener;
import eu.darkcube.system.vanillaaddons.module.ModuleManager;
import eu.darkcube.system.vanillaaddons.module.modules.flightchestplate.FlightChestplateModule;
import eu.darkcube.system.vanillaaddons.module.modules.recipes.RecipesModule;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.TeleporterModule;
import eu.darkcube.system.vanillaaddons.module.modules.worldmechanics.WorldMechanicsModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class VanillaAddons extends DarkCubePlugin {
	private InventoryRegistry inventoryRegistry;
	private UserModifier userModifier;
	private final ModuleManager moduleManager = new ModuleManager();
	private static VanillaAddons instance = null;

	public VanillaAddons() {
		instance = this;
	}

	public static VanillaAddons instance() {
		return instance;
	}

	@Override
	public void onLoad() {
		moduleManager.addModule(new RecipesModule(this));
		moduleManager.addModule(new FlightChestplateModule(this));
		moduleManager.addModule(new TeleporterModule(this));
		moduleManager.addModule(new WorldMechanicsModule(this));
	}

	@Override
	public void onDisable() {
		moduleManager.disableAll();
		UserAPI.getInstance().removeModifier(userModifier);
	}

	@Override
	public void onEnable() {
		inventoryRegistry = new InventoryRegistry();
		UserAPI.getInstance().addModifier(userModifier = new Modifier(this));
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new ArmorListener(), this);
		moduleManager.enableAll();
	}

	public ModuleManager moduleManager() {
		return moduleManager;
	}

	public InventoryRegistry inventoryRegistry() {
		return inventoryRegistry;
	}
}
