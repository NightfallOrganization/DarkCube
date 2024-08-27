/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.team;
import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.teamArgument;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;
import static eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType.integer;

import java.util.Objects;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.game.lobby.Lobby;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;

public class SetLifesCommand extends WoolBattleCommand {
    public SetLifesCommand() {
        // @formatter:off
        super("setlifes", b -> b
                .then(argument("team", teamArgument())
                        .requires(s -> s.game() != null && s.game().phase() instanceof Lobby)
                        .then(argument("lifes", integer(0, 999))
                                .executes(ctx -> {
                                    // @formatter:on
                                    var team = team(ctx, "team");
                                    var lifes = getInteger(ctx, "lifes");
                                    team.lifes(lifes);
                                    ctx.getSource().sendMessage(Messages.CHANGED_LIFES_FOR_TEAM, team.getName(ctx.getSource().source()), lifes);
                                    return 0;
                                    // @formatter:off
                                })
                        )
                ).then(argument("lifes", integer(-1, 999))
                        .requires(s -> s.game() != null && s.game().phase() instanceof Lobby)
                        .executes(ctx -> {
                            // @formatter:on
                            var lifes = getInteger(ctx, "lifes");
                            var lobby = ((Lobby) Objects.requireNonNull(ctx.getSource().game()).phase());
                            lobby.forceLifes(lifes);
                            ctx.getSource().sendMessage(Messages.CHANGED_LIFES, lifes);
                            return 0;
                            // @formatter:off
                        })
                )
        );
        // @formatter:on
    }
}
