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
