/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class Skyland extends JavaPlugin {

	private static Skyland instance;

	public Skyland() {
		instance = this;
	}

	public static Skyland getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
		System.out.println("disable");
		CustomArmor.onDisable(this);
	}

	@Override
	public void onEnable() {
		try {
			Language.GERMAN.registerLookup(getClassLoader(), "messages_de.properties",
					s -> Message.PREFIX + s);
			Language.ENGLISH.registerLookup(getClassLoader(), "messages_en.properties",
					s -> Message.PREFIX + s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		instance.getCommand("gm").setExecutor(new GM());
		instance.getCommand("heal").setExecutor(new Heal());
		instance.getCommand("day").setExecutor(new Day());
		instance.getCommand("night").setExecutor(new Night());
		instance.getCommand("fly").setExecutor(new Fly());
		instance.getCommand("feed").setExecutor(new Feed());
		instance.getCommand("max").setExecutor(new Max());
		instance.getCommand("god").setExecutor(new God());
		instance.getCommand("trash").setExecutor(new Trash());
		instance.getCommand("world").setExecutor(new WorldX());
		instance.getCommand("loadworld").setExecutor(new Loadworld());
		instance.getCommand("unloadworld").setExecutor(new UnloadWorld());
		instance.getCommand("createworld").setExecutor(new CreateWorld());
		instance.getCommand("getitem").setExecutor(new GetItem());
		TrainingStand trainingStand = new TrainingStand();
		instance.getCommand("spawntrainingstand").setExecutor(trainingStand);
		Bukkit.getPluginManager().registerEvents(trainingStand, this);
		CustomArmor.onEnable(this);
	}
}