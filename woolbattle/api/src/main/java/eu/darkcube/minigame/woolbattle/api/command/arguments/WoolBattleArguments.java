/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.command.arguments;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
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

    @NotNull
    ArgumentType<@NotNull ?> mapArgument0();

    @NotNull
    Map map0(@NotNull CommandContext<?> ctx, @NotNull String name);

    @NotNull
    ArgumentType<@NotNull ?> entityArgument0(boolean player, boolean single);

    @NotNull
    Collection<? extends Entity> entities0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

    @NotNull
    Entity entity0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

    @NotNull
    Collection<? extends WBUser> players0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

    @NotNull
    WBUser player0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

    @NotNull
    ArgumentType<?> teamArgument0(boolean allowSpectator);

    @NotNull
    Team team0(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException;

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

    static @NotNull ArgumentType<@NotNull ?> mapArgument() {
        return instance().mapArgument0();
    }

    static @NotNull Map map(@NotNull CommandContext<?> ctx, @NotNull String name) {
        return instance().map0(ctx, name);
    }

    static @NotNull ArgumentType<@NotNull ?> entityArgument() {
        return instance().entityArgument0(false, true);
    }

    static @NotNull ArgumentType<@NotNull ?> entitiesArgument() {
        return instance().entityArgument0(false, false);
    }

    static @NotNull ArgumentType<@NotNull ?> playerArgument() {
        return instance().entityArgument0(true, true);
    }

    static @NotNull ArgumentType<@NotNull ?> playersArgument() {
        return instance().entityArgument0(true, false);
    }

    static @NotNull Collection<? extends Entity> entities(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return instance().entities0(ctx, name);
    }

    static @NotNull Entity entity(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return instance().entity0(ctx, name);
    }

    static @NotNull Collection<? extends WBUser> players(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return instance().players0(ctx, name);
    }

    static @NotNull WBUser player(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return instance().player0(ctx, name);
    }

    static @NotNull ArgumentType<?> teamArgument() {
        return instance().teamArgument0(true);
    }

    static @NotNull ArgumentType<?> teamArgumentNoSpectator() {
        return instance().teamArgument0(false);
    }

    static @NotNull Team team(@NotNull CommandContext<?> ctx, String name) throws CommandSyntaxException {
        return instance().team0(ctx, name);
    }

    private static WoolBattleArguments instance() {
        return WoolBattleApi.instance().commandArguments();
    }
}
