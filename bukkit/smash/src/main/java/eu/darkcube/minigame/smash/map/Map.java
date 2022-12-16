/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.map;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import eu.darkcube.minigame.smash.Main;
import eu.darkcube.minigame.smash.util.Locations;
import eu.darkcube.minigame.smash.util.MaterialAndId;

public class Map {

	private static YamlConfiguration cfg;
	private static Gson gson;

	private static abstract class Serializer implements JsonSerializer<Location>, JsonDeserializer<Location> {

	}

	public static final Set<Map> MAPS = new HashSet<>();
	
	public static YamlConfiguration getConfig() {
		return cfg;
	}

	static {
		cfg = Main.getInstance().getConfig("maps");
		for (String key : cfg.getKeys(false)) {
			fromJson(cfg.getString(key));
		}
		gson = new GsonBuilder().registerTypeAdapter(Location.class, new Serializer() {
			@Override
			public Location deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2)
					throws JsonParseException {
				return Locations.deserialize(arg0.getAsJsonPrimitive().getAsString(), Locations.DEFAULT_LOCATION);
			}
			@Override
			public JsonElement serialize(Location arg0, Type arg1, JsonSerializationContext arg2) {
				return new JsonPrimitive(Locations.serialize(arg0));
			}
		}).create();
	}

	private static Map fromJson(String json) {
		Map map = new Gson().fromJson(json, Map.class);
		return map;
	}

	private final String name;
	private int deathHeight;
	private boolean enabled;
	private MaterialAndId icon;
	private Location spawn = Locations.DEFAULT_LOCATION;

	public Map() {
		MAPS.add(this);
		this.name = null;
	}

	public Map(String name) {
		MAPS.add(this);
		this.name = name;
		this.deathHeight = 0;
		this.enabled = false;
		this.icon = new MaterialAndId(Material.GRASS);
		save();
	}

	public boolean isEnabled() {
		return enabled;
	}

	public int getDeathHeight() {
		return deathHeight;
	}

	public MaterialAndId getIcon() {
		return icon;
	}

	public void setDeathHeight(int deathHeight) {
		this.deathHeight = deathHeight;
		save();
	}

	public void setIcon(MaterialAndId icon) {
		this.icon = icon;
		save();
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		save();
	}

	public void delete() {
		MAPS.remove(this);
		cfg.set(name, null);
		Main.getInstance().saveConfig(cfg);
	}

	private void save() {
		String json = serialize();
		cfg.set(name, json);
		Main.getInstance().saveConfig(cfg);
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
		save();
	}

	public Location getSpawn() {
		return spawn;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return serialize();
	}

	public String serialize() {
		return gson.toJson(this);
	}
	
	public static Map deserialize(String json) {
		return gson.fromJson(json, Map.class);
	}
}
