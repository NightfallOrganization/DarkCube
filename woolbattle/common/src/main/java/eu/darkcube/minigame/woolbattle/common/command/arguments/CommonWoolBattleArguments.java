/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonWoolBattleArguments implements WoolBattleArguments {
    @Override
    public @NotNull ArgumentType<@NotNull CommonGame> gameArgument0(@NotNull WoolBattleApi woolbattle) {
        return CommonGameArgument.gameArgument((CommonWoolBattleApi) woolbattle);
    }

    @Override
    public @NotNull CommonGame game0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return CommonGameArgument.game(ctx, name);
    }
}
