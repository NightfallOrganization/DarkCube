/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class ColorUtil {

	@SuppressWarnings("deprecation")
	public static byte byDyeColor(DyeColor color) {
		return color.getData();
	}

	public static byte byDyeColor(String color) {
		for (DyeColor dye : DyeColor.values()) {
			String name = dye.name();
			String name2 = dye.name().replace("_", "");
			if (name.equalsIgnoreCase(color) || name2.equalsIgnoreCase(color)) {
				return byDyeColor(dye);
			}
		}
		return 0;
	}
	
	public static char byChatColor(ChatColor color) {
		return color.getChar();
	}
	
	public static char byChatColor(String color) {
		for (ChatColor cht : ChatColor.values()) {
			String name = cht.name();
			String name2 = cht.name().replace("_", "");
			if (name.equalsIgnoreCase(color) || name2.equalsIgnoreCase(color)) {
				return byChatColor(cht);
			}
		}
		return 'f';
	}
}
