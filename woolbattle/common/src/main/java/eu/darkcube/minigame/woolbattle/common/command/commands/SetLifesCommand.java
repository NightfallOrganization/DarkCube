/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands;

import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class SetLifesCommand extends WoolBattleCommand {
    public SetLifesCommand(CommonWoolBattle woolbattle) {
        super("setlifes", b -> b.requires(s -> s.source() instanceof CommonWBUser).then(argument("team", WoolBattleArguments.gameArgument())));
    }
}
