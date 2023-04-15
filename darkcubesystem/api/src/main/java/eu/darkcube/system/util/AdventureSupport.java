/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitAudiences;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;

import java.util.Objects;

public class AdventureSupport {

	private static BukkitAudiences audienceProvider;

	public static BukkitAudiences audienceProvider() {
		if (audienceProvider == null) {
			audienceProvider = BukkitAudiences.create(DarkCubePlugin.systemPlugin());
		}
		return audienceProvider;
	}

	public static Style convert(ChatColor color) {
		switch (color) {
			case BLACK:
				return Style.style(NamedTextColor.BLACK);
			case DARK_BLUE:
				return Style.style(NamedTextColor.DARK_BLUE);
			case DARK_GREEN:
				return Style.style(NamedTextColor.DARK_GREEN);
			case DARK_AQUA:
				return Style.style(NamedTextColor.DARK_AQUA);
			case DARK_RED:
				return Style.style(NamedTextColor.DARK_RED);
			case DARK_PURPLE:
				return Style.style(NamedTextColor.DARK_PURPLE);
			case GOLD:
				return Style.style(NamedTextColor.GOLD);
			case GRAY:
				return Style.style(NamedTextColor.GRAY);
			case DARK_GRAY:
				return Style.style(NamedTextColor.DARK_GRAY);
			case BLUE:
				return Style.style(NamedTextColor.BLUE);
			case GREEN:
				return Style.style(NamedTextColor.GREEN);
			case AQUA:
				return Style.style(NamedTextColor.AQUA);
			case RED:
				return Style.style(NamedTextColor.RED);
			case LIGHT_PURPLE:
				return Style.style(NamedTextColor.LIGHT_PURPLE);
			case YELLOW:
				return Style.style(NamedTextColor.YELLOW);
			case WHITE:
				return Style.style(NamedTextColor.WHITE);
			case MAGIC:
				return Style.style(TextDecoration.OBFUSCATED);
			case BOLD:
				return Style.style(TextDecoration.BOLD);
			case STRIKETHROUGH:
				return Style.style(TextDecoration.STRIKETHROUGH);
			case UNDERLINE:
				return Style.style(TextDecoration.UNDERLINED);
			case ITALIC:
				return Style.style(TextDecoration.ITALIC);
			default:
				throw new IllegalArgumentException(Objects.toString(color));
		}
	}
}
