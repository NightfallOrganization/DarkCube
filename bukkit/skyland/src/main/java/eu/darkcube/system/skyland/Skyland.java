/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.skyland.Listener.SkylandListener;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayerModifier;
import eu.darkcube.system.skyland.inventoryUI.AllInventory;
import eu.darkcube.system.skyland.mobs.CustomMob;
import eu.darkcube.system.skyland.mobs.FollowingMob;
import eu.darkcube.system.skyland.worldGen.CustomChunkGenerator;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Skyland extends DarkCubePlugin {

	ArrayList<CustomMob> mobs = new ArrayList<>();

	private static Skyland instance;

	public Skyland() {
		super("skyland");
		instance = this;
	}

	public static Skyland getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
		System.out.println("disable");
	}

	@Override
	public void onEnable() {


/*		try {
			Language.GERMAN.registerLookup(getClassLoader(), "messages_de.properties",
					s -> Message.PREFIX + s);
			Language.ENGLISH.registerLookup(getClassLoader(), "messages_en.properties",
					s -> Message.PREFIX + s);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		*/



		SkylandListener damageListener = new SkylandListener(this);
		Bukkit.getPluginManager().registerEvents(damageListener, instance);
		Bukkit.getPluginManager().registerEvents(AllInventory.getInstance(), instance);

		instance.getCommand("gm").setExecutor(new GM());
		instance.getCommand("heal").setExecutor(new Heal());
		instance.getCommand("day").setExecutor(new Day());
		instance.getCommand("night").setExecutor(new Night());
		instance.getCommand("fly").setExecutor(new Fly());
		instance.getCommand("test").setExecutor(new Feed());
		instance.getCommand("max").setExecutor(new Max());
		instance.getCommand("god").setExecutor(new God());
		instance.getCommand("trash").setExecutor(new Trash());
		instance.getCommand("world").setExecutor(new WorldX());
		instance.getCommand("loadworld").setExecutor(new Loadworld());
		instance.getCommand("unloadworld").setExecutor(new UnloadWorld());
		instance.getCommand("createworld").setExecutor(new CreateWorld());
		instance.getCommand("getitem").setExecutor(new GetItem());
		instance.getCommand("getgui").setExecutor(new GetGUI());
		TrainingStand trainingStand = new TrainingStand();
		instance.getCommand("spawntrainingstand").setExecutor(trainingStand);
		Bukkit.getPluginManager().registerEvents(trainingStand, this);

		new BukkitRunnable() {
			@Override
			public void run() {
				for (CustomMob cm : mobs){
					cm.aiTick();
				}
			}
		}.runTaskTimer(this, 20, 1);


	}

	public ArrayList<CustomMob> getMobs() {
		return mobs;
	}
	public CustomMob getCustomMob(Mob mob){
		for (CustomMob customMob : mobs){
			if (mob.equals(customMob.getMob())){
				return customMob;
			}
		}

		if (mob.getPersistentDataContainer().has(CustomMob.getCustomMobTypeKey())){
			int id = mob.getPersistentDataContainer().get(CustomMob.getCustomMobTypeKey(), PersistentDataType.INTEGER);
			if (id == 0){
				return new FollowingMob(mob);
			}
		}

		return null;
	}

	public void removeCustomMob(Mob m){
		for (CustomMob cm: mobs) {

			if (cm.getMob().equals(m)){
				mobs.remove(cm);
				System.out.println("mob removed");
				return;
			}

		}

		System.out.println("no mob removed");
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
		System.out.println("custom gen loaded!!");
		return new CustomChunkGenerator();
	}

}
