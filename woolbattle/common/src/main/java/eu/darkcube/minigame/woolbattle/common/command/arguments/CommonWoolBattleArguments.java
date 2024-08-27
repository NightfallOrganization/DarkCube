/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.command.arguments.ToStringFunction;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.arguments.entity.EntitySelectorOptions;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class CommonWoolBattleArguments implements WoolBattleArguments {
    private final CommonWoolBattleApi api;

    public CommonWoolBattleArguments(CommonWoolBattleApi api) {
        this.api = api;
        EntitySelectorOptions.bootStrap();
    }

    @Override
    public @NotNull ArgumentType<?> gameArgument0() {
        return CommonGameArgument.gameArgument(api);
    }

    @Override
    public @NotNull Game game0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return CommonGameArgument.game(ctx, name);
    }

    @Override
    public @NotNull ArgumentType<@NotNull ?> teamConfigurationArgument0() {
        return CommonTeamConfigurationArgument.teamConfiguration(api);
    }

    @Override
    public @NotNull <T extends TeamConfiguration> ArgumentType<@NotNull ?> teamConfigurationArgument0(@NotNull ToStringFunction<@NotNull T> toStringFunction) {
        return CommonTeamConfigurationArgument.teamConfiguration(api, toStringFunction);
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
    public @NotNull ArgumentType<@NotNull ?> woolColorArgument0() {
        return WoolColorArgument.woolColor(api.woolProvider());
    }

    @Override
    public @NotNull ColoredWool woolColor0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return WoolColorArgument.getWoolColor(ctx, name);
    }

    @Override
    public @NotNull ArgumentType<@NotNull ?> textColorArgument0() {
        return TextColorArgument.color();
    }

    @Override
    public @NotNull TextColor textColor0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return TextColorArgument.getColor(ctx, name);
    }

    @Override
    public @NotNull ArgumentType<@NotNull MapSize> mapSizeArgument0() {
        return CommonMapSizeArgument.mapSize(api);
    }

    @Override
    public @NotNull ArgumentType<@NotNull MapSize> mapSizeArgument0(@NotNull Predicate<@NotNull MapSize> validator) {
        return CommonMapSizeArgument.mapSize(api, validator);
    }

    @Override
    public @NotNull MapSize mapSize0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return CommonMapSizeArgument.getMapSize(ctx, name);
    }

    @Override
    public @NotNull ArgumentType<@NotNull ?> mapArgument0() {
        return CommonMapArgument.map(api);
    }

    @Override
    public @NotNull Map map0(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return CommonMapArgument.getMap(ctx, name);
    }

    @Override
    public @NotNull ArgumentType<@NotNull ?> entityArgument0(boolean player, boolean single) {
        return new CommonEntityArgument(api, player, single);
    }

    @Override
    public @NotNull Collection<? extends Entity> entities0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return CommonEntityArgument.get(ctx, name).findEntities((CommandSource) ctx.getSource());
    }

    @Override
    public @NotNull Entity entity0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return CommonEntityArgument.get(ctx, name).findSingleEntity((CommandSource) ctx.getSource());
    }

    @Override
    public @NotNull Collection<? extends WBUser> players0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return CommonEntityArgument.get(ctx, name).findPlayers((CommandSource) ctx.getSource());
    }

    @Override
    public @NotNull WBUser player0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return CommonEntityArgument.get(ctx, name).findSinglePlayer((CommandSource) ctx.getSource());
    }

    @Override
    public @NotNull ArgumentType<?> teamArgument0(boolean allowSpectator) {
        return CommonTeamArgument.team(api, allowSpectator);
    }

    @Override
    public @NotNull Team team0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return CommonTeamArgument.team(ctx, name);
    }
}
