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
import eu.darkcube.system.util.Language;
import eu.darkcube.system.vanillaaddons.AUser.Modifier;
import eu.darkcube.system.vanillaaddons.inventory.InventoryRegistry;
import eu.darkcube.system.vanillaaddons.listener.ArmorListener;
import eu.darkcube.system.vanillaaddons.listener.InventoryListener;
import eu.darkcube.system.vanillaaddons.module.ModuleManager;
import eu.darkcube.system.vanillaaddons.module.modules.RandoShit;
import eu.darkcube.system.vanillaaddons.module.modules.actionbar.ActionbarModule;
import eu.darkcube.system.vanillaaddons.module.modules.anvilmechanics.AnvilMechanicsModule;
import eu.darkcube.system.vanillaaddons.module.modules.colors.ColorsModule;
import eu.darkcube.system.vanillaaddons.module.modules.deathchests.DeathChestsModule;
import eu.darkcube.system.vanillaaddons.module.modules.messaging.MessagingModule;
import eu.darkcube.system.vanillaaddons.module.modules.onlinetime.OnlinetimeModule;
import eu.darkcube.system.vanillaaddons.module.modules.recipes.RecipesModule;
import eu.darkcube.system.vanillaaddons.module.modules.rtp.RTPModule;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.TeleporterModule;
import eu.darkcube.system.vanillaaddons.module.modules.worldmechanics.WorldMechanicsModule;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

import java.io.IOException;
import java.util.function.Function;

public class VanillaAddons extends DarkCubePlugin {
	private static VanillaAddons instance = null;
	private final ModuleManager moduleManager = new ModuleManager();
	private InventoryRegistry inventoryRegistry;
	private UserModifier userModifier;

	public VanillaAddons() {
		super("vanillaaddons");
		instance = this;
	}

	public static VanillaAddons instance() {
		return instance;
	}

	@Override
	public void onLoad() {
		try {
			Function<String, String> keyModifier = key -> "VANILLAADDONS_" + key;
			Language.GERMAN.registerLookup(getClass().getClassLoader(), "messages_de.properties",
					keyModifier);
			Language.ENGLISH.registerLookup(getClass().getClassLoader(), "messages_en.properties",
					keyModifier);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		moduleManager.addModule(new RecipesModule(this));
		//		moduleManager.addModule(new FlightChestplateModule(this));
		moduleManager.addModule(new TeleporterModule(this));
		moduleManager.addModule(new WorldMechanicsModule(this));
		moduleManager.addModule(new AnvilMechanicsModule(this));
		moduleManager.addModule(new ColorsModule(this));
		moduleManager.addModule(new MessagingModule(this));
		moduleManager.addModule(new RTPModule());
		moduleManager.addModule(new OnlinetimeModule());
		moduleManager.addModule(new DeathChestsModule(this));
		moduleManager.addModule(new RandoShit(this));
		moduleManager.addModule(new ActionbarModule());
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
