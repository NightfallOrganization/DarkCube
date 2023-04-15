/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import org.bukkit.Location;

import eu.darkcube.minigame.woolbattle.util.MaterialAndId;

public interface Map {

	boolean isEnabled();
	int getDeathHeight();
	MaterialAndId getIcon();
	
	void setDeathHeight(int height);
	void setIcon(MaterialAndId icon);
	
	void enable();
	void disable();
	void delete();
	
	void setSpawn(String name, Location loc);
	Location getSpawn(String name);
	
	String getName();
	
	String serialize();
}
