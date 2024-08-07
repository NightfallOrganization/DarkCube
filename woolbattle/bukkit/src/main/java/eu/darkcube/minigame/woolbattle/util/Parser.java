/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;

public class Parser {

    public static boolean parseBoolean(String s) {
        return Boolean.parseBoolean(s);
    }

    public static int parseInt(String s) {
        return Integer.parseInt(s);
    }

    public static double parseDouble(String s) {
        return Double.parseDouble(s);
    }

    public static DyeColor parseDyeColor(String s) {
        for (DyeColor color : DyeColor.values()) {
            if (color.name().toLowerCase().equals(s.toLowerCase())) return color;
        }
        return null;
    }

    public static float parseFloat(String s) {
        return Float.parseFloat(s);
    }

    public static World parseWorld(String s) {
        if (s.equals("null")) {
            return null;
        }
        return Bukkit.getWorld(s);
    }
}
