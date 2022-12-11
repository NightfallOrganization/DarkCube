/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.arguments.ServiceTaskArgument;
import net.md_5.bungee.api.ChatColor;

public class CommandRemoveTask extends LobbyCommandExecutor {

	private static final DynamicCommandExceptionType TASK_NOT_PRESENT =
			Message.SERVICE_TASK_NOT_PRESENT.newDynamicCommandExceptionType();

	public CommandRemoveTask() {
		super("removeTask", b -> b.then(Commands.argument("task", new ArgumentType<String>() {
			@Override
			public String parse(StringReader reader) throws CommandSyntaxException {
				String task = reader.readString();
				if (!Lobby.getInstance().getDataManager().getWoolBattleTasks().contains(task)) {
					throw TASK_NOT_PRESENT.createWithContext(reader,task);
				}
				return task;
			}

			@Override
			public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
					SuggestionsBuilder builder) {
				return ISuggestionProvider.suggest(
						Lobby.getInstance().getDataManager().getWoolBattleTasks(), builder);
			}
		}).executes(ctx -> {
			String task = ctx.getArgument("task", String.class);
			Set<String> tasks = Lobby.getInstance().getDataManager().getWoolBattleTasks();
			tasks.remove(task);
			Lobby.getInstance().getDataManager().setWoolBattleTasks(tasks);
			ctx.getSource().sendFeedback(
					new CustomComponentBuilder("Task erfolgreich entfernt!").color(ChatColor.GREEN)
							.create(), true);
			return 0;
		})));
	}

}
