/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.command.arguments;

import java.util.function.Predicate;
import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface WoolBattleArguments {
    @NotNull
    ArgumentType<@NotNull ?> gameArgument0();

    @NotNull
    Game game0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

    @NotNull
    ArgumentType<@NotNull ?> teamConfigurationArgument0();

    <T extends TeamConfiguration> @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument0(@NotNull ToStringFunction<@NotNull T> toStringFunction);

    <T extends TeamConfiguration> @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument0(@NotNull Supplier<@NotNull T @NotNull []> supplier);

    <T extends TeamConfiguration> @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument0(@NotNull Supplier<@NotNull T @NotNull []> supplier, @NotNull ToStringFunction<@NotNull T> toStringFunction);

    @NotNull
    TeamConfiguration teamConfiguration0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

    @NotNull
    ArgumentType<@NotNull ?> woolColorArgument0();

    @NotNull
    ColoredWool woolColor0(@NotNull CommandContext<?> ctx, @NotNull String name);

    @NotNull
    ArgumentType<@NotNull ?> textColorArgument0();

    @NotNull
    TextColor textColor0(@NotNull CommandContext<?> ctx, @NotNull String name);

    @NotNull
    ArgumentType<@NotNull ?> mapSizeArgument0();

    @NotNull
    ArgumentType<@NotNull ?> mapSizeArgument0(@NotNull Predicate<@NotNull MapSize> validator);

    @NotNull
    MapSize mapSize0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

    static @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument() {
        return instance().teamConfigurationArgument0();
    }

    static <T extends TeamConfiguration> @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument(@NotNull ToStringFunction<@NotNull T> toStringFunction) {
        return instance().teamConfigurationArgument0(toStringFunction);
    }

    static <T extends TeamConfiguration> @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument(@NotNull Supplier<@NotNull T @NotNull []> supplier) {
        return instance().teamConfigurationArgument0(supplier);
    }

    static <T extends TeamConfiguration> @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument(@NotNull Supplier<@NotNull T @NotNull []> supplier, @NotNull ToStringFunction<@NotNull T> toStringFunction) {
        return instance().teamConfigurationArgument0(supplier, toStringFunction);
    }

    static @NotNull TeamConfiguration teamConfiguration(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return instance().teamConfiguration0(ctx, name);
    }

    static @NotNull ArgumentType<@NotNull ?> gameArgument() {
        return instance().gameArgument0();
    }

    static @NotNull Game game(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return instance().game0(ctx, name);
    }

    static @NotNull ArgumentType<@NotNull ?> mapSizeArgument() {
        return instance().mapSizeArgument0();
    }

    static @NotNull ArgumentType<@NotNull ?> mapSizeArgument(@NotNull Predicate<MapSize> validator) {
        return instance().mapSizeArgument0(validator);
    }

    static @NotNull MapSize mapSize(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return instance().mapSize0(ctx, name);
    }

    static @NotNull ArgumentType<@NotNull ?> woolColorArgument() {
        return instance().woolColorArgument0();
    }

    static @NotNull ColoredWool woolColor(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return instance().woolColor0(ctx, name);
    }

    static @NotNull ArgumentType<@NotNull ?> textColorArgument() {
        return instance().textColorArgument0();
    }

    static @NotNull TextColor textColor(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return instance().textColor0(ctx, name);
    }

    private static WoolBattleArguments instance() {
        return WoolBattleApi.instance().commandArguments();
    }
}
