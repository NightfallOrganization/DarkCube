/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

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
                    Component send = Component
                            .text("Generated: " + text, NamedTextColor.GRAY)
                            .clickEvent(ClickEvent.copyToClipboard(text))
                            .hoverEvent(HoverEvent.showText(Component.text("Copy " + text.codePoints().count() + " Characters")))
                            .append(Component.newline())
                            .append(Component.text("Real Width: ", NamedTextColor.GRAY))
                            .append(Component.text(realWidth, NamedTextColor.GOLD));
                    ctx.getSource().sendMessage(send);
                    return 0;
                })))
                .then(Commands.literal("width").then(Commands.argument("text", StringArgumentType.greedyString()).executes(ctx -> {
                    String text = StringArgumentType.getString(ctx, "text");
                    ctx
                            .getSource()
                            .sendMessage(Component
                                    .text("Width: ", NamedTextColor.GRAY)
                                    .append(Component.text(plugin.glyphWidthManager().width(text), NamedTextColor.AQUA)));
                    return 0;
                })))
                .then(Commands.literal("nearest").then(Commands.argument("width", FloatArgumentType.floatArg()).executes(ctx -> {
                    float width = FloatArgumentType.getFloat(ctx, "width");
                    int cp = plugin.glyphWidthManager().nearestCodepoint(width);
                    ctx.getSource().sendMessage(Component.text("Codepoint: " + cp + "(" + plugin.glyphWidthManager().width(cp) + ")"));
                    return 0;
                }))));
    }
}
