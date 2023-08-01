/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetSpawn;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.*;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;

public class CommandTeam extends WBCommandExecutor {
    public CommandTeam(WoolBattle woolbattle) {

        super("team", b -> b
                .then(Commands.argument("mapSize", MapSizeArgument.mapSize(woolbattle))
                        .then(Commands.argument("team", TeamTypeArgument.teamTypeArgument(woolbattle, new MapSizeToString()))
                                .then(new CommandDisable().builder())
                                .then(new CommandEnable().builder())
                                .then(new CommandInfo().builder())
                                .then(new CommandSetNameColor().builder())
                                .then(new CommandSetSpawn(woolbattle).builder())
                                .then(new CommandSetWoolColor().builder())
                                .then(new CommandDelete().builder())
                        )
                )
        );
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
