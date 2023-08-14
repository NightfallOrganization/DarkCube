/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.Citybuild;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.FloatArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.ClickEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.event.HoverEvent;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;

public class GlyphCommand extends CommandExecutor {
    public GlyphCommand(Citybuild plugin) {
        super("citybuild", "glyph", new String[0], b -> b
                .then(Commands.literal("generate").then(Commands.argument("width", FloatArgumentType.floatArg()).executes(ctx -> {
                    float width = FloatArgumentType.getFloat(ctx, "width");
                    String text = plugin.glyphWidthManager().spacesForWidth(width);
                    float realWidth = plugin.glyphWidthManager().width(text);
                    Component comp = Component
                            .text("Generated: " + text, NamedTextColor.GRAY)
                            .clickEvent(ClickEvent.copyToClipboard(text))
                            .hoverEvent(HoverEvent.showText(Component
                                    .text("Copy", NamedTextColor.GOLD)
                                    .append(Component.text(" " + text.codePoints().count() + " Characters", NamedTextColor.GRAY))))
                            .append(Component.newline())
                            .append(Component.text("Real Width: ", NamedTextColor.GRAY))
                            .append(Component.text(realWidth, NamedTextColor.AQUA));
                    ctx.getSource().sendMessage(comp);
                    return 0;
                })))
                .then(Commands.literal("width").then(Commands.argument("text", StringArgumentType.greedyString()).executes(ctx -> {
                    String text = StringArgumentType.getString(ctx, "text");
                    Component comp = Component
                            .text("Width: ", NamedTextColor.GRAY)
                            .append(Component.text(plugin.glyphWidthManager().width(text), NamedTextColor.AQUA));
                    ctx.getSource().sendMessage(comp);
                    return 0;
                })))
                .then(Commands.literal("nearest").then(Commands.argument("width", FloatArgumentType.floatArg()).executes(ctx -> {
                    float width = FloatArgumentType.getFloat(ctx, "width");
                    int cp = plugin.glyphWidthManager().nearestCodepoint(width);
                    float realWidth = plugin.glyphWidthManager().width(cp);
                    Component comp = Component
                            .text("Best Glyph: Id=" + cp, NamedTextColor.GRAY)
                            .hoverEvent(HoverEvent.showText(Component.text("Copy", NamedTextColor.GOLD)))
                            .clickEvent(ClickEvent.copyToClipboard(new String(new int[]{cp}, 0, 1)))
                            .append(Component.newline())
                            .append(Component.text("Real Width: ", NamedTextColor.GRAY))
                            .append(Component.text(realWidth, NamedTextColor.AQUA));
                    ctx.getSource().sendMessage(comp);
                    return 0;
                }))));
    }
}
