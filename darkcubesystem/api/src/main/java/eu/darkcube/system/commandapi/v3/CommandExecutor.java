/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.function.Consumer;

public class CommandExecutor {

	private String name;
	private String prefix;
	private String permission;
	private String[] aliases;
	private String[] names;
	private Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder;

	public CommandExecutor(String prefix, String name, String[] aliases,
			Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(prefix, name, prefix + "." + name, aliases, argumentBuilder);
	}

	public CommandExecutor(String prefix, String name, String permission, String[] aliases,
			Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this.prefix = prefix;
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
		this.argumentBuilder = argumentBuilder;
		this.names = new String[this.aliases.length + 1];
		this.names[0] = name;
		System.arraycopy(this.aliases, 0, this.names, 1, this.names.length - 1);
	}

	public final LiteralArgumentBuilder<CommandSource> builder() {
		LiteralArgumentBuilder<CommandSource> b = Commands.literal(name);
		argumentBuilder.accept(b);
		return b;
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
