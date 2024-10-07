/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command;

import java.util.Locale;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommands;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.KickCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.ListEntitiesCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.SetLifesCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.SettingsCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.TeleportCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.TimerCommand;
import eu.darkcube.minigame.woolbattle.common.command.commands.WoolBattleRootCommand;
import eu.darkcube.system.libs.com.mojang.brigadier.CommandDispatcher;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.RequiredArgumentBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public abstract class CommonWoolBattleCommands implements WoolBattleCommands {
    protected final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    public void registerDefaults(CommonWoolBattleApi woolbattle) {
        register(new WoolBattleRootCommand(woolbattle));
        register(new SettingsCommand());
        register(new TimerCommand());
        register(new KickCommand());
        register(new TeleportCommand());
        register(new SetLifesCommand());
        register(new ListEntitiesCommand());
    }

    @Override
    public final void register(@NotNull WoolBattleCommand command) {
        for (var name : command.names()) {
            name = name.toLowerCase(Locale.ROOT);
            var builder = command.builder(name);
            var node = dispatcher.register(builder);
            register0(command, name, node);
        }
        register0(command);
    }

    @Override
    public final void unregister(@NotNull WoolBattleCommand command) {
        for (var name : command.names()) {
            var node = (LiteralCommandNode<CommandSource>) dispatcher.getRoot().getChild(name);
            dispatcher.getRoot().getChildren().remove(node);
            unregister0(command, name, node);
        }
        unregister0(command);
    }

    protected void unregister0(WoolBattleCommand command) {
    }

    protected void unregister0(WoolBattleCommand command, String name, LiteralCommandNode<CommandSource> node) {
    }

    protected void register0(WoolBattleCommand command) {
    }

    protected void register0(WoolBattleCommand command, String name, LiteralCommandNode<CommandSource> node) {
    }

    public static LiteralArgumentBuilder<CommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
