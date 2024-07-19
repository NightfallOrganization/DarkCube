/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.function.Predicate;
import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.command.arguments.ToStringFunction;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonWoolBattleArguments implements WoolBattleArguments {
    private final CommonWoolBattleApi woolbattle;

    public CommonWoolBattleArguments(CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public @NotNull CommonGameArgument gameArgument0() {
        return CommonGameArgument.gameArgument(woolbattle);
    }

    @Override
    public @NotNull CommonGame game0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return CommonGameArgument.game(ctx, name);
    }

    @Override
    public @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument0() {
        return CommonTeamConfigurationArgument.teamConfiguration(woolbattle);
    }

    @Override
    public @NotNull <T extends TeamConfiguration> ArgumentType<@NotNull ?> teamConfigurationArgument0(@NotNull ToStringFunction<@NotNull T> toStringFunction) {
        return CommonTeamConfigurationArgument.teamConfiguration(woolbattle, toStringFunction);
    }

    @Override
    public @NotNull <T extends TeamConfiguration> ArgumentType<@NotNull ?> teamConfigurationArgument0(@NotNull Supplier<@NotNull T @NotNull []> supplier) {
        return CommonTeamConfigurationArgument.teamConfiguration(supplier);
    }

    @Override
    public @NotNull <T extends TeamConfiguration> ArgumentType<@NotNull ?> teamConfigurationArgument0(@NotNull Supplier<@NotNull T @NotNull []> supplier, @NotNull ToStringFunction<@NotNull T> toStringFunction) {
        return CommonTeamConfigurationArgument.teamConfiguration(supplier, toStringFunction);
    }

    @Override
    public @NotNull TeamConfiguration teamConfiguration0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return CommonTeamConfigurationArgument.getTeamConfiguration(ctx, name);
    }

    @Override
    public @NotNull ArgumentType<@NotNull MapSize> mapSizeArgument0() {
        return CommonMapSizeArgument.mapSize();
    }

    @Override
    public @NotNull ArgumentType<@NotNull MapSize> mapSizeArgument0(@NotNull Predicate<@NotNull MapSize> validator) {
        return CommonMapSizeArgument.mapSize(validator);
    }

    @Override
    public @NotNull MapSize mapSize0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return CommonMapSizeArgument.getMapSize(ctx, name);
    }
}
