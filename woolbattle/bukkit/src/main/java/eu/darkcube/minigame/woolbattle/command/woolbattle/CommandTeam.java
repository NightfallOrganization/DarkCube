/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandDelete;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandDisable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandEnable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandInfo;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetNameColor;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetSpawn;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetWoolColor;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;

public class CommandTeam extends WBCommand {
    public CommandTeam(WoolBattleBukkit woolbattle) {
        // @formatter:off
        super("team", b ->
                b.then(Commands.argument("mapSize", MapSizeArgument.mapSize(woolbattle, new MapSizeValidator(woolbattle)))
                        .then(Commands.argument("team", TeamTypeArgument.teamTypeArgument(woolbattle, new MapSizeToString()))
                                .then(new CommandDisable().builder())
                                .then(new CommandEnable().builder())
                                .then(new CommandInfo().builder())
                                .then(new CommandSetNameColor().builder())
                                .then(new CommandSetSpawn(woolbattle).builder())
                                .then(new CommandSetWoolColor().builder())
                                .then(new CommandDelete().builder()))
                )
        );
        // @formatter:on
    }

    private static class MapSizeValidator implements Predicate<MapSize> {
        private final WoolBattleBukkit woolbattle;

        public MapSizeValidator(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @Override
        public boolean test(MapSize mapSize) {
            return woolbattle.teamManager().teamTypes().stream().map(TeamType::mapSize).distinct().toList().contains(mapSize);
        }
    }

    private static class MapSizeToString implements TeamTypeArgument.ToStringFunction {
        private final TeamTypeArgument.ToStringFunction parent = TeamTypeArgument.ToStringFunction.function();

        @Override
        public <S> String[] toString(CommandContext<S> context, TeamType teamType) {
            MapSize mapSize = MapSizeArgument.mapSize(context, "mapSize");
            if (mapSize.equals(teamType.mapSize())) {
                return parent.toString(context, teamType);
            }
            return new String[0];
        }
    }
}
