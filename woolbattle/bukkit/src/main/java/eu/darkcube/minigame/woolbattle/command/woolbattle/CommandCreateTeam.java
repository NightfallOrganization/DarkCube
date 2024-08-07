/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import static eu.darkcube.system.bukkit.commandapi.Commands.argument;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EnumArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

public class CommandCreateTeam extends WBCommand {
    public CommandCreateTeam(WoolBattleBukkit woolbattle) {
        super("createTeam", b -> b.then(argument("team", StringArgumentType.word()).then(argument("weight", IntegerArgumentType.integer()).then(argument("woolColor", EnumArgument.enumArgument(DyeColor.values(), d -> new String[]{d.name().toLowerCase()})).then(argument("nameColor", EnumArgument.enumArgument(SupportedColors.values())).then(Commands.argument("mapSize", MapSizeArgument.mapSize(woolbattle)).executes(ctx -> {
            var teamDNK = StringArgumentType.getString(ctx, "team");
            var mapSize = MapSizeArgument.mapSize(ctx, "mapSize");
            var teamType = woolbattle.teamManager().byDisplayNameKey(mapSize, teamDNK);
            if (teamType != null) {
                ctx.getSource().sendMessage(Component.text("Es existiert bereits ein Team mit dem Namen " + teamDNK));
                return 0;
            }
            var values = woolbattle.teamManager().teamTypes(mapSize);
            var weight = IntegerArgumentType.getInteger(ctx, "weight");
            for (var type : values) {
                if (type.getWeight() == weight) {
                    ctx.getSource().sendMessage(Component.text("Ein Team hat bereits die Sortierung " + weight));
                }
            }
            var woolColor = EnumArgument.getEnumArgument(ctx, "woolColor", DyeColor.class);
            var supportedNameColor = EnumArgument.getEnumArgument(ctx, "nameColor", SupportedColors.class);
            var nameColor = ChatColor.valueOf(supportedNameColor.name());
            teamType = woolbattle.teamManager().create(mapSize, teamDNK, weight, woolColor, nameColor, false);
            teamType.save();
            ctx.getSource().sendMessage(Component.text("Du hast ein neues Team mit dem Namen '" + teamDNK + "', der Sortierung " + weight + ", der Wollfarbe " + woolColor.name() + ", der Namenfarbe " + nameColor.name() + " erstellt!").append(Component.newline().append(Component.text("Bedenke, dass du den Server neustarten musst damit das Team erkannt wird!"))));
            return 0;
        })))))));
    }

    public enum SupportedColors {
        RED, BLUE, BLACK, GRAY, GREEN, WHITE, PURPLE, YELLOW
    }
}
