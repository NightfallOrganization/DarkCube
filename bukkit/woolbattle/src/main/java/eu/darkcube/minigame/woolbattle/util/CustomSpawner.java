/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.TNTPrimed;

public class CustomSpawner {

	public static TNTPrimed spawnTNT(Location loc, Entity source) {
		TNTPrimed tnt = loc.getWorld().spawn(loc, TNTPrimed.class);
		return tnt;
	}
	
}
