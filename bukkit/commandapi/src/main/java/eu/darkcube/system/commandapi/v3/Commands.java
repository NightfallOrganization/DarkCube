/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import org.bukkit.command.CommandSender;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Commands {

//	private static final Logger LOGGER = LogManager.getLogger();
	private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

	public List<Suggestion> getTabCompletions(CommandSender sender, String commandLine) {
		CommandSource source = CommandSource.create(sender);
		StringReader reader = new StringReader(commandLine);
		ParseResults<CommandSource> parseresults = getDispatcher().parse(reader, source);
		try {
			return dispatcher.getCompletionSuggestions(parseresults).get().getList();
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		} catch (ExecutionException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public int executeCommand(CommandSender sender, String commandLine) {
		CommandSource source = CommandSource.create(sender);
		try {
			return dispatcher.execute(commandLine, source);
		} catch (CommandSyntaxException ex) {
			source.sendErrorMessage(ex.getRawMessage());
//			source.sendErrorMessage(CustomComponentBuilder.cast(TextComponent.fromLegacyText(ex.getMessage())));
			List<Suggestion> completions = getTabCompletions(sender, commandLine);
			if (completions.isEmpty()) {
				commandLine = commandLine + " ";
				completions = getTabCompletions(sender, commandLine);
			}
			source.getSource().sendCompletions(commandLine, completions);
		} catch (Throwable ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			String[] msgs = writer.getBuffer().toString().replace("	", "    ").split(System.lineSeparator());
			Arrays.asList(msgs).forEach(msg -> {
				source.getSource()
						.sendMessage(b -> b.append("").color(ChatColor.DARK_RED),
								CustomComponentBuilder.cast(TextComponent.fromLegacyText(msg)));
			});
		}
		return 0;
	}

	public static LiteralArgumentBuilder<CommandSource> literal(String s) {
		return LiteralArgumentBuilder.literal(s);
	}

	public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

	public static Predicate<String> predicate(Commands.IParser parser) {
		return bool -> {
			try {
				parser.parse(new StringReader(bool));
				return true;
			} catch (CommandSyntaxException ex) {
				return false;
			}
		};
	}

	public CommandDispatcher<CommandSource> getDispatcher() {
		return dispatcher;
	}

	@FunctionalInterface
	public interface IParser {
		void parse(StringReader reader) throws CommandSyntaxException;
	}
}
