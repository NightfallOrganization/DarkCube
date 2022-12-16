/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class WarpPoint {

	String name, worldName, icon;
	double[] coords = new double[5];
	boolean enabled;

	public WarpPoint(String name, Location loc, String icon, boolean enabled) {
		this(name, null, 0, 0, 0, 0, 0, icon, enabled);
		if (loc != null)
			setLocation(loc);
	}

	public WarpPoint(String name) {
		this(name, "world", 0, 0, 0, 0, 0, null, false);
	}

	public WarpPoint(String name, String worldName, double x, double y, double z, float yaw, float pitch, String icon,
			boolean enabled) {
		this.name = name;
		this.worldName = worldName;
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
		coords[3] = yaw;
		coords[4] = pitch;
		this.icon = icon;
		this.enabled = enabled;
	}

	public String getName() {
		return name;
	}

	public Location getLocation() {
		if (Bukkit.getWorld(worldName) != null) {
			return new Location(Bukkit.getWorld(worldName), coords[0], coords[1], coords[2], (float) coords[3],
					(float) coords[4]);
		}
		return null;
	}

	public String getIcon() {
		return icon;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(Location loc) {
		if (loc != null && loc.getWorld() != null)
			setLocation(loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
		else {
			worldName = null;
			coords[0] = 0;
			coords[1] = 0;
			coords[2] = 0;
			coords[3] = 0;
			coords[4] = 0;
		}
	}

	public void setLocation(String worldName, double x, double y, double z, float yaw, float pitch) {
		this.worldName = worldName;
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
		coords[3] = yaw;
		coords[4] = pitch;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isValid() {
		if (name.contains(" "))
			name = name.split(" ")[0];

		if (icon != null) {
			if (icon.split(":").length == 1)
				icon = icon + ":0";
			if (Material.valueOf(icon.split(":")[0]) == null)
				icon = null;
		}

		if (enabled && (name == null || getLocation() == null || icon == null))
			enabled = false;

		return (name != null && getLocation() != null && icon != null);
	}
}
