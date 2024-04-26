/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.command.arguments;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface WoolBattleArguments {
    @NotNull ArgumentType<@NotNull ? extends Game> gameArgument0(@NotNull WoolBattleApi woolbattle);

    @NotNull Game game0(@NotNull CommandContext<?> ctx, @NotNull String name);

    static @NotNull ArgumentType<@NotNull ? extends Game> gameArgument(@NotNull WoolBattleApi woolbattle) {
        return instance().gameArgument0(woolbattle);
    }

    static @NotNull Game game(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return instance().game0(ctx, name);
    }

    private static WoolBattleArguments instance() {
        return InjectionLayer.ext().instance(WoolBattleArguments.class);
    }
}
