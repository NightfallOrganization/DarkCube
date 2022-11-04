package eu.darkcube.system.commandapi.v3;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

public class CommandExecutor {

	private String name;
	private String prefix;
	private String permission;
	private String[] aliases;
	private String[] names;
	private TriConsumer<CommandDispatcher<CommandSource>, LiteralCommandNode<CommandSource>, LiteralArgumentBuilder<CommandSource>> argumentBuilder;

	public CommandExecutor(String prefix, String name, String[] aliases,
					Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(prefix, name, prefix + "." + name, aliases, argumentBuilder);
	}

	public CommandExecutor(String prefix, String name, String permission,
					String[] aliases,
					Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(prefix, name, permission, aliases, (node, builder) -> {
			argumentBuilder.accept(builder);
		});
	}

	public CommandExecutor(String prefix, String name, String permission,
					String[] aliases,
					BiConsumer<LiteralCommandNode<CommandSource>, LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(prefix, name, permission, aliases, (dispatcher, node, builder) -> {
			argumentBuilder.accept(node, builder);
		});
	}

	public CommandExecutor(String prefix, String name, String permission,
					String[] aliases,
					TriConsumer<CommandDispatcher<CommandSource>, LiteralCommandNode<CommandSource>, LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this.prefix = prefix;
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
		this.argumentBuilder = argumentBuilder;
		this.names = new String[this.aliases.length + 1];
		this.names[0] = name;
		for (int i = 1; i < this.names.length; i++) {
			this.names[i] = this.aliases[i - 1];
		}
	}

	public void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal(this.name);
		LiteralCommandNode<CommandSource> node = dispatcher.register(builder);
		builder = Commands.literal(this.name);
		this.argumentBuilder.accept(dispatcher, node, builder);
		node = dispatcher.register(builder);

		for (String name : getNames()) {
			dispatcher.register(buildRedirect(name, node));
		}
	}

	protected LiteralArgumentBuilder<CommandSource> buildRedirect(
					final String alias,
					final LiteralCommandNode<CommandSource> destination) {
		// Redirects only work for nodes with children, but break the top
		// argument-less command.
		// Manually adding the root command after setting the redirect doesn't
		// fix it.
		// See https://github.com/Mojang/brigadier/issues/46). Manually clone
		// the node instead.
		LiteralArgumentBuilder<CommandSource> builder = Commands.literal(alias.toLowerCase(Locale.ENGLISH)).requires(destination.getRequirement()).forward(destination.getRedirect(), destination.getRedirectModifier(), destination.isFork()).executes(destination.getCommand());
		for (CommandNode<CommandSource> child : destination.getChildren()) {
			builder.then(child);
		}
		return builder;
	}

	public String[] getAliases() {
		return aliases;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPermission() {
		return permission;
	}

	public String[] getNames() {
		return names;
	}

	public static interface TriConsumer<A, B, C> {

		public void accept(A a, B b, C c);

	}
}
