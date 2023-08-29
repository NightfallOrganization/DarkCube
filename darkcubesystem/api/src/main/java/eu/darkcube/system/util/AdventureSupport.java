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
        return switch (color) {
            case BLACK -> Style.style(NamedTextColor.BLACK);
            case DARK_BLUE -> Style.style(NamedTextColor.DARK_BLUE);
            case DARK_GREEN -> Style.style(NamedTextColor.DARK_GREEN);
            case DARK_AQUA -> Style.style(NamedTextColor.DARK_AQUA);
            case DARK_RED -> Style.style(NamedTextColor.DARK_RED);
            case DARK_PURPLE -> Style.style(NamedTextColor.DARK_PURPLE);
            case GOLD -> Style.style(NamedTextColor.GOLD);
            case GRAY -> Style.style(NamedTextColor.GRAY);
            case DARK_GRAY -> Style.style(NamedTextColor.DARK_GRAY);
            case BLUE -> Style.style(NamedTextColor.BLUE);
            case GREEN -> Style.style(NamedTextColor.GREEN);
            case AQUA -> Style.style(NamedTextColor.AQUA);
            case RED -> Style.style(NamedTextColor.RED);
            case LIGHT_PURPLE -> Style.style(NamedTextColor.LIGHT_PURPLE);
            case YELLOW -> Style.style(NamedTextColor.YELLOW);
            case WHITE -> Style.style(NamedTextColor.WHITE);
            case MAGIC -> Style.style(TextDecoration.OBFUSCATED);
            case BOLD -> Style.style(TextDecoration.BOLD);
            case STRIKETHROUGH -> Style.style(TextDecoration.STRIKETHROUGH);
            case UNDERLINE -> Style.style(TextDecoration.UNDERLINED);
            case ITALIC -> Style.style(TextDecoration.ITALIC);
            default -> throw new IllegalArgumentException(Objects.toString(color));
        };
    }
}
