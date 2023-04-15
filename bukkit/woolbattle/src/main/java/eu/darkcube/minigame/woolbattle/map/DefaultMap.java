/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.util.Locations;
import eu.darkcube.minigame.woolbattle.util.MaterialAndId;
import eu.darkcube.minigame.woolbattle.util.Serializable;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.HashMap;

public class DefaultMap implements Map, Serializable {

	private final java.util.Map<String, Location> spawns;
	private String name;
	private int deathHeight;
	private boolean enabled;
	private MaterialAndId icon;

	DefaultMap() {
		spawns = null;
	}

	public DefaultMap(String name) {
		this.name = name;
		spawns = new HashMap<>();
		enabled = false;
		icon = new MaterialAndId(Material.GRASS);
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public int getDeathHeight() {
		return deathHeight;
	}

	@Override
	public MaterialAndId getIcon() {
		return icon;
	}

	@Override
	public void setIcon(MaterialAndId icon) {
		this.icon = icon;
		save();
	}

	@Override
	public void enable() {
		enabled = true;
		save();
	}

	@Override
	public void disable() {
		enabled = false;
		save();
	}

	@Override
	public void delete() {
		WoolBattle.instance().getMapManager().deleteMap(this);
	}

	@Override
	public void setSpawn(String name, Location loc) {
		spawns.put(name, loc);
		save();
	}

	@Override
	public Location getSpawn(String name) {
		if (spawns == null)
			return null;
		Location spawn = spawns.get(name);
		return spawn != null ? spawn : Locations.DEFAULT_LOCATION;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String serialize() {
		return Serializable.super.serialize();
	}

	@Override
	public void setDeathHeight(int height) {
		this.deathHeight = height;
		save();
	}

	private void save() {
		WoolBattle.instance().getMapManager().saveMaps();
	}
}
