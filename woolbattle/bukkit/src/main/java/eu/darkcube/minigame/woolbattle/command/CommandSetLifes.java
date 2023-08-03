/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.argument.TeamTypeArgument;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandSetLifes extends WBCommandExecutor {

    public CommandSetLifes(WoolBattle woolbattle) {
        super("setlifes", "woolbattle.command.setlifes", new String[0], b -> b
                .then(Commands
                        .argument("lifes", IntegerArgumentType.integer(0, 99))
                        .requires(source -> woolbattle.lobby().enabled())
                        .executes(context -> {
                            woolbattle.gameData().forceLifes(IntegerArgumentType.getInteger(context, "lifes"));
                            context.getSource().sendMessage(Message.CHANGED_LIFES.getServerMessage(woolbattle.gameData().forceLifes()));
                            return 0;
                        }))
                .then(Commands
                        .argument("team", TeamTypeArgument.teamTypeArgument(woolbattle))
                        .requires(source -> woolbattle.ingame().enabled())
                        .then(Commands.argument("lifes", IntegerArgumentType.integer(0, 99)).executes(context -> {
                            Team team = woolbattle.teamManager().getTeam(TeamTypeArgument.teamType(context, "team"));
                            team.setLifes(IntegerArgumentType.getInteger(context, "lifes"));
                            context.getSource().sendMessage(Component.text("Leben gesetzt!"));
                            return 0;
                        }))));
    }

}
