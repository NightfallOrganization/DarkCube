/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.reflect.TypeToken;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;

public class DefaultMapManager implements MapManager {

	private final java.util.Map<String, Map> MAPS;

	public DefaultMapManager(String maps) {
		if (maps == null) {
			MAPS = new HashMap<>();
		} else {
			MAPS = GsonSerializer.gson.fromJson(maps, new TypeToken<java.util.Map<String, Map>>() {
				private static final long serialVersionUID = -4394658839136612803L;
			}.getType());
		}
	}

	public DefaultMapManager() {
		this(WoolBattle.getInstance().getConfig("spawns").getString("maps"));
	}

	@Override
	public void deleteMap(Map map) {
		MAPS.remove(map.getName());
		saveMaps();
	}

	@Override
	public void saveMaps() {
		String json = GsonSerializer.gson.toJson(MAPS);
		YamlConfiguration cfg = WoolBattle.getInstance().getConfig("spawns");
		cfg.set("maps", json);
		WoolBattle.getInstance().saveConfig(cfg);
	}

	@Override
	public Map getMap(String name) {
		return MAPS.get(name);
	}

	@Override
	public Map createMap(String name) {
		Map map = getMap(name);
		if (map != null)
			return map;
		map = new DefaultMap(name);
		MAPS.put(name, map);
		saveMaps();
		return map;
	}

	@Override
	public Collection<? extends Map> getMaps() {
		return Collections.unmodifiableCollection(MAPS.values());
	}
}
