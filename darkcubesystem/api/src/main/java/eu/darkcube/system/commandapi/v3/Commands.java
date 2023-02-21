/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.com.mojang.brigadier.Command;
import eu.darkcube.system.libs.com.mojang.brigadier.CommandDispatcher;
import eu.darkcube.system.libs.com.mojang.brigadier.ParseResults;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.RequiredArgumentBuilder;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestion;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;
import eu.darkcube.system.libs.com.mojang.brigadier.tree.LiteralCommandNode;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.command.CommandSender;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

public class Commands {

	private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
	private final Collection<CommandEntry> commandEntries = new HashSet<>();

	public static LiteralArgumentBuilder<CommandSource> literal(String s) {
		return LiteralArgumentBuilder.literal(s);
	}

	public static <T> RequiredArgumentBuilder<CommandSource, T> argument(String name,
			ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

	public static Predicate<String> predicate(IParser parser) {
		return bool -> {
			try {
				parser.parse(new StringReader(bool));
				return true;
			} catch (CommandSyntaxException ex) {
				return false;
			}
		};
	}

	public List<Suggestion> getTabCompletions(CommandSender sender, String commandLine) {
		CommandSource source = CommandSource.create(sender);
		StringReader reader = new StringReader(commandLine);
		ParseResults<CommandSource> parseresults = getDispatcher().parse(reader, source);
		try {
			return dispatcher.getCompletionSuggestions(parseresults).get().getList();
		} catch (InterruptedException | ExecutionException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void unregisterByPrefix(String prefix) {
		for (CommandEntry entry : new HashSet<>(commandEntries)) {
			if (!entry.executor.getPrefix().equals(prefix)) {
				continue;
			}
			for (CommandEntry.OriginalCommandTree original : new HashSet<>(entry.nodes)) {
				if (unregister(dispatcher.getRoot(), original)) {
					VersionSupport.getVersion().commandApi().unregister(original.source.getName());
					entry.nodes.remove(original);
				}
			}
			if (entry.nodes.isEmpty()) {
				commandEntries.remove(entry);
			}
		}
	}

	public void unregisterPrefixlessByPrefix(String prefix) {
		for (CommandEntry entry : new HashSet<>(commandEntries)) {
			if (!entry.executor.getPrefix().equals(prefix)) {
				continue;
			}
			for (CommandEntry.OriginalCommandTree original : new HashSet<>(entry.nodes)) {
				if (original.prefixless) {
					if (unregister(dispatcher.getRoot(), original)) {
						VersionSupport.getVersion().commandApi()
								.unregister(original.source.getName());
						entry.nodes.remove(original);
					}
				}
			}
			if (entry.nodes.isEmpty()) {
				commandEntries.remove(entry);
			}
		}
	}

	private boolean unregister(CommandNode<CommandSource> parent,
			CommandEntry.OriginalCommandTree original) {
		CommandNode<CommandSource> node = parent.getChild(original.source.getName());
		if (node == null)
			return false;
		for (CommandEntry.OriginalCommandTree o : original.children) {
			unregister(node, o);
		}
		Command<CommandSource> ncommand = node.getCommand();
		if (ncommand != null && ncommand.equals(original.command)) {
			ncommand = null;
		}
		if (ncommand == null && node.getChildren().isEmpty()) {
			parent.getChildren().remove(node);
			return true;
		}
		return false;
	}

	public void register(CommandExecutor executor) {
		Collection<CommandEntry.OriginalCommandTree> nodes = new HashSet<>();
		for (String name : executor.getNames()) {
			nodes.add(new CommandEntry.OriginalCommandTree(
					dispatcher.register(executor.builder(name)), true));
			nodes.add(new CommandEntry.OriginalCommandTree(dispatcher.register(
					executor.builder(executor.getPrefix() + ":" + executor.getName())), false));
		}
		commandEntries.add(new CommandEntry(executor, nodes));
	}

	private LiteralArgumentBuilder<CommandSource> buildRedirect(final String alias,
			final LiteralCommandNode<CommandSource> destination) {
		// Redirects only work for nodes with children, but break the top argument - less command.
		// Manually adding the root command after setting the redirect doesn't fix it.
		// See https://github.com/Mojang/brigadier/issues/46). Manually clone the node instead.
		LiteralArgumentBuilder<CommandSource> builder =
				Commands.literal(alias.toLowerCase(Locale.ENGLISH))
						.requires(destination.getRequirement())
						.forward(destination.getRedirect(), destination.getRedirectModifier(),
								destination.isFork()).executes(destination.getCommand());
		for (CommandNode<CommandSource> child : destination.getChildren()) {
			builder.then(child);
		}
		return builder;
	}

	@SuppressWarnings("unused") // This inspection is not accurate, we use it in version specific
	// code. IntelliJ just has some problems finding that code
	public void executeCommand(CommandSender sender, String commandLine) {
		CommandSource source = CommandSource.create(sender);
		try {
			dispatcher.execute(commandLine, source);
		} catch (CommandSyntaxException ex) {
			source.sendMessage(ex.getRawMessage());
			List<Suggestion> completions = getTabCompletions(sender, commandLine);
			if (completions.isEmpty()) {
				commandLine = commandLine + " ";
				completions = getTabCompletions(sender, commandLine);
			}
			source.getSource().sendCompletions(commandLine, completions);
		} catch (Throwable ex) {
			StringWriter writer = new StringWriter();
			ex.printStackTrace(new PrintWriter(writer));
			String[] msgs = writer.getBuffer().toString().replace(/*tab*/"	", "    ")
					.split("(\r\n|\r|\n)");
			Component c = Component.text("");
			for (int i = 0; i < msgs.length; i++) {
				if (i != 0)
					c = c.appendNewline();
				c = c.append(Component.text(msgs[i]).color(NamedTextColor.DARK_RED));
			}
			source.sendMessage(c);
		}
	}

	public CommandDispatcher<CommandSource> getDispatcher() {
		return dispatcher;
	}

	@FunctionalInterface
	public interface IParser {
		void parse(StringReader reader) throws CommandSyntaxException;
	}

	private static class CommandEntry {
		private final CommandExecutor executor;
		private final Collection<OriginalCommandTree> nodes;

		public CommandEntry(CommandExecutor executor, Collection<OriginalCommandTree> nodes) {
			this.executor = executor;
			this.nodes = nodes;
		}

		@Override
		public String toString() {
			return "CommandEntry{" + "executor=" + executor + ", nodes=" + nodes + '}';
		}

		private static class OriginalCommandTree {
			private final Command<CommandSource> command;
			private final CommandNode<CommandSource> source;
			private final Collection<OriginalCommandTree> children;
			private final boolean prefixless;

			public OriginalCommandTree(CommandNode<CommandSource> node, boolean prefixless) {
				this.source = node;
				this.command = node.getCommand();
				this.children = new HashSet<>();
				this.prefixless = prefixless;
				for (CommandNode<CommandSource> child : node.getChildren()) {
					children.add(new OriginalCommandTree(child, false));
				}
			}

			@Override
			public String toString() {
				return "OriginalCommandTree{" + "command=" + command + ", source=" + source
						+ ", children=" + children + ", prefixless=" + prefixless + '}';
			}
		}
	}
}
