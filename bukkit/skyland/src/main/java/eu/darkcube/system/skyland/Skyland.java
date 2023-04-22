/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland;

import eu.darkcube.system.skyland.Listener.SkylandListener;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import eu.darkcube.system.skyland.inventoryUI.AllInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class Skyland extends JavaPlugin {


	ArrayList<SkylandPlayer> players = new ArrayList<>();
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


	}

	public SkylandPlayer getSkylandPlayers(Player player) {
		for (SkylandPlayer sp : players){
			if (sp != null) {
				if (player.equals(sp.getPlayer())){
					return sp;
				}
			}
		}
		System.out.println("No SkylandPlayer found for player " + player.getName());
		return null;
	}

	public void addSkylandPlayer(SkylandPlayer p){
		players.add(p);
		if (p == null) {
			System.out.println("p was null!");
		}
	}
}
