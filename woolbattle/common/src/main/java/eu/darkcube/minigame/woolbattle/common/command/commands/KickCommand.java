/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.players;
import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.playersArgument;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;

public class KickCommand extends WoolBattleCommand {
    public KickCommand() {
        super("kick", b -> b.then(argument("targets", playersArgument()).executes(ctx -> {
            var players = players(ctx, "targets");
            for (var player : players) {
                player.kick();
            }
            return 0;
        })));
    }
}
