/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetSpawn;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.*;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;

import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CommandTeam extends WBCommandExecutor {
    public CommandTeam(WoolBattleBukkit woolbattle) {

        super("team", b -> b.then(Commands
                .argument("mapSize", MapSizeArgument.mapSize(woolbattle, new MapSizeValidator(woolbattle)))
                .then(Commands
                        .argument("team", TeamTypeArgument.teamTypeArgument(woolbattle, new MapSizeToString()))
                        .then(new CommandDisable().builder())
                        .then(new CommandEnable().builder())
                        .then(new CommandInfo().builder())
                        .then(new CommandSetNameColor().builder())
                        .then(new CommandSetSpawn(woolbattle).builder())
                        .then(new CommandSetWoolColor().builder())
                        .then(new CommandDelete().builder()))));
    }

    private static class MapSizeValidator implements Predicate<MapSize> {
        private final WoolBattleBukkit woolbattle;

        public MapSizeValidator(WoolBattleBukkit woolbattle) {
            this.woolbattle = woolbattle;
        }

        @Override
        public boolean test(MapSize mapSize) {
            return woolbattle
                    .teamManager()
                    .teamTypes()
                    .stream()
                    .map(TeamType::mapSize)
                    .distinct()
                    .collect(Collectors.toList())
                    .contains(mapSize);
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
