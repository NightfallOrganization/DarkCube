/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattlemodern;

import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;

public class CommandRemoveTask extends LobbyCommand {

    private static final DynamicCommandExceptionType TASK_NOT_PRESENT = Messages.SERVICE_TASK_NOT_PRESENT.newDynamicCommandExceptionType();

    public CommandRemoveTask() {
        super("removeTask", b -> b.then(Commands.argument("task", new ArgumentType<String>() {
            @Override
            public String parse(StringReader reader) throws CommandSyntaxException {
                var task = reader.readString();
                if (!Lobby.getInstance().getDataManager().getWoolBattleModernTasks().contains(task)) {
                    throw TASK_NOT_PRESENT.createWithContext(reader, task);
                }
                return task;
            }

            @Override
            public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
                return ISuggestionProvider.suggest(Lobby.getInstance().getDataManager().getWoolBattleModernTasks(), builder);
            }
        }).executes(ctx -> {
            var task = ctx.getArgument("task", String.class);
            var tasks = new HashSet<>(Lobby.getInstance().getDataManager().getWoolBattleModernTasks());
            tasks.remove(task);
            Lobby.getInstance().getDataManager().setWoolBattleModernTasks(tasks);
            ctx.getSource().sendMessage(Component.text("Task erfolgreich entfernt!").color(NamedTextColor.GREEN));
            return 0;
        })));
    }

}
