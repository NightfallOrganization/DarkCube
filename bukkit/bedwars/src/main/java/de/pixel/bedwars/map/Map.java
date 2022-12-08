/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.map;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.Charsets;
import org.bukkit.Location;
import org.bukkit.Material;

import com.google.gson.JsonSyntaxException;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.BedwarsGson;
import de.pixel.bedwars.util.DontSerialize;
import de.pixel.bedwars.util.Locations;
import de.pixel.bedwars.util.MaterialAndId;

public class Map {

	private static Set<Map> maps = new HashSet<>();

	@DontSerialize
	private String name;
	private MaterialAndId icon;
	private java.util.Map<String, Location> spawns;
	private java.util.Map<String, Location> beds;

	static {
		File folder = getFolder();
		folder.mkdirs();
		for (File file : folder.listFiles()) {
			if (file.getName().endsWith(".map")) {
				try {
					BufferedReader r = new BufferedReader(
							new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
					Map map = BedwarsGson.GSON.fromJson(r, Map.class);
					map.name = file.getName().substring(0, file.getName().length() - 4);
					r.close();
					Main.getInstance().sendConsole("§aLoaded map " + map.name);
				} catch (FileNotFoundException ex) {
					ex.printStackTrace();
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (JsonSyntaxException ex) {
					System.out.println("Invalid map-file " + file.getName() + ":");
					ex.printStackTrace();
				}
			}
		}
	}

	public static Map getMap(String name) {
		for (Map map : getMaps()) {
			if (map.name.equals(name)) {
				return map;
			}
		}
		return null;
	}

	public static Set<Map> getMaps() {
		return Collections.unmodifiableSet(maps);
	}

	public static Set<String> getAllMapNames() {
		Set<String> names = new HashSet<>();
		for (Map map : getMaps()) {
			names.add(map.getName());
		}
		return names;
	}

	public static void init() {

	}

	private Map() {
		maps.add(this);
		this.beds = new HashMap<>();
	}

	public Map(String name) {
		this(name, new MaterialAndId(Material.GRASS));
	}

	public Map(String name, MaterialAndId icon) {
		this(name, icon, new HashMap<>());
	}

	public Map(String name, MaterialAndId icon, java.util.Map<String, Location> spawns) {
		this();
		this.name = name;
		this.icon = icon;
		this.spawns = spawns;
		save();
	}

	public String getName() {
		return name;
	}

	public MaterialAndId getIcon() {
		return icon;
	}

	public void setIcon(MaterialAndId icon) {
		this.icon = icon;
		save();
	}

	public Location getSpawn(String key) {
		return spawns.get(key);
	}

	public void setBed(Team team, Location bed) {
		beds.put(team.getTranslationName(), bed);
		save();
	}
	
	public Location getBedSafe(Team team) {
		return beds.getOrDefault(team.getTranslationName(), Locations.DEFAULT_LOCATION);
	}

	public Location getBed(Team team) {
		return beds.get(team.getTranslationName());
	}

	public Location getSpawnSafe(String key) {
		return spawns.getOrDefault(key, Locations.DEFAULT_LOCATION);
	}

	public Map setSpawn(String key, Location value) {
		spawns.put(key, value);
		save();
		return this;
	}

	private static File getFolder() {
		File folder = new File(Main.getInstance().getDataFolder(), "maps");
		return folder;
	}

	private File getFile() {
		File file = new File(getFolder(), name + ".map");
		return file;
	}

	public void delete() {
		File f = getFile();
		maps.remove(this);
		if (f.exists()) {
			f.delete();
		}
	}

	public void save() {
		String json = BedwarsGson.GSON.toJson(this);
		File f = getFile();
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		try {
			BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), Charsets.UTF_8));
			w.write(json);
			w.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
