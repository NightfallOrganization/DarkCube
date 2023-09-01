/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EnumArgument;
import eu.darkcube.system.bukkit.commandapi.argument.StringArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;

import java.util.Collection;

import static eu.darkcube.system.bukkit.commandapi.Commands.argument;

public class CommandCreateTeam extends WBCommand {
    public CommandCreateTeam(WoolBattleBukkit woolbattle) {
        super("createTeam", b -> b.then(argument("team", StringArgument.string(Arrays
                .asList(SupportedColors.values())
                .stream()
                .map(SupportedColors::name)
                .toArray(String[]::new))).then(argument("weight", IntegerArgumentType.integer()).then(argument("woolColor", EnumArgument.enumArgument(DyeColor.values(), d -> new String[]{d.name().toLowerCase()})).then(argument("nameColor", EnumArgument.enumArgument(SupportedColors.values())).then(Commands
                .argument("mapSize", MapSizeArgument.mapSize(woolbattle))
                .executes(ctx -> {
                    String teamDNK = StringArgument.getString(ctx, "team");
                    MapSize mapSize = MapSizeArgument.mapSize(ctx, "mapSize");
                    TeamType teamType = woolbattle.teamManager().byDisplayNameKey(mapSize, teamDNK);
                    if (teamType != null) {
                        ctx.getSource().sendMessage(Component.text("Es existiert bereits ein Team mit dem Namen " + teamDNK));
                        return 0;
                    }
                    Collection<TeamType> values = woolbattle.teamManager().teamTypes(mapSize);
                    int weight = IntegerArgumentType.getInteger(ctx, "weight");
                    for (TeamType type : values) {
                        if (type.getWeight() == weight) {
                            ctx.getSource().sendMessage(Component.text("Ein Team hat bereits die Sortierung " + weight));
                        }
                    }
                    DyeColor woolColor = EnumArgument.getEnumArgument(ctx, "woolColor", DyeColor.class);
                    SupportedColors snameColor = EnumArgument.getEnumArgument(ctx, "nameColor", SupportedColors.class);
                    ChatColor nameColor = ChatColor.valueOf(snameColor.name());
                    teamType = woolbattle.teamManager().create(mapSize, teamDNK, weight, woolColor, nameColor, false);
                    teamType.save();
                    ctx
                            .getSource()
                            .sendMessage(Component
                                    .text("Du hast ein neues Team mit dem Namen '" + teamDNK + "', der Sortierung " + weight + ", der Wollfarbe " + woolColor.name() + ", der Namenfarbe " + nameColor.name() + " erstellt!")
                                    .append(Component
                                            .newline()
                                            .append(Component.text("Bedenke, dass du den Server neustarten musst damit das Team erkannt wird!"))));
                    return 0;
                })))))));
    }

    public enum SupportedColors {
        RED, BLUE, BLACK, GRAY, GREEN, WHITE, PURPLE, YELLOW
    }
}
