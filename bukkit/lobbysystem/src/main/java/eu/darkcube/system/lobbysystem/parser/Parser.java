/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.parser;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;

public class Parser {

	public static boolean parseBoolean(String s) {
		if (s.equalsIgnoreCase("false")) {
			return false;
		} else if (s.equalsIgnoreCase("true")) {
			return true;
		} else {
			throw new IllegalArgumentException("Can't input else than true or false to parser");
		}
	}

	public static int parseInt(String s) {
		return Integer.parseInt(s);
	}

	public static double parseDouble(String s) {
		return Double.parseDouble(s);
	}

	public static DyeColor parseDyeColor(String s) {
		for (DyeColor color : DyeColor.values()) {
			if (color.name().toLowerCase().equals(s.toLowerCase()))
				return color;
		}
		return null;
	}

	public static float parseFloat(String s) {
		return Float.parseFloat(s);
	}

	public static World parseWorld(String s) {
		return Bukkit.getWorld(s);
	}
}
