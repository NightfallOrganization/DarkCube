/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.spawner.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.spawner.ItemSpawner;
import de.pixel.bedwars.util.Locations;

public class SpawnerIO {

	private static File file;
	private static YamlConfiguration cfg;
	
	static {
		file = new File(Main.getInstance().getDataFolder(), "spawners.yml");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		cfg = YamlConfiguration.loadConfiguration(file);
	}

	public static void save(ItemSpawner spawner) {
		List<String> list = getSpawnersStringList();
		list.add(toString(spawner));
		setSpawnersStringList(list);
	}
	
	public static void remove(ItemSpawner spawner) {
		List<String> list = getSpawnersStringList();
		list.remove(toString(spawner));
		setSpawnersStringList(list);
	}

	public static ItemSpawner fromString(String json) {
		JsonObject object = new Gson().fromJson(json, JsonObject.class);
		String clazzName = object.get("class").getAsString();
		Class<?> clazz;
		try {
			clazz = Class.forName(clazzName);
			ItemSpawner spawner = (ItemSpawner) clazz.newInstance();
			Location loc = Locations.deserialize(object.get("loc").getAsString(), null);
			spawner.setBlock(loc.getBlock());
			return spawner;
		} catch (ClassNotFoundException ex) {
			throw new Error(ex);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		throw new Error();
	}

	public static String toString(ItemSpawner spawner) {
		JsonObject object = new JsonObject();
		String clazz = spawner.getClass().getName();
		object.addProperty("class", clazz);
		String location = Locations.serialize(spawner.getSpawnerBlock().getLocation());
		object.addProperty("loc", location);
		return object.toString();
	}

	public static List<String> getSpawnersStringList() {
		return cfg.getStringList("spawners");
	}

	public static void setSpawnersStringList(List<String> list) {
		cfg.set("spawners", list);
		try {
			cfg.save(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
