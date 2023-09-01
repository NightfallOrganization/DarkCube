/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.function.Consumer;

public class WBCommand extends Command {
	public WBCommand(String name,
					 Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(name, new String[0], argumentBuilder);
	}

	public WBCommand(String name, String[] aliases,
					 Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		super("woolbattle", name, aliases, argumentBuilder);
	}

	public WBCommand(String name, String permission, String[] aliases,
					 Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		super("woolbattle", name, permission, aliases, argumentBuilder);
	}
}
