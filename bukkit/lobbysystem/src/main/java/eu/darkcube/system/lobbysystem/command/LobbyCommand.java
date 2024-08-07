/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.function.Consumer;

public class LobbyCommand extends Command {

	public LobbyCommand(String name, String[] aliases,
						Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		super("lobby", name, aliases, argumentBuilder);
	}

	public LobbyCommand(String name,
						Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(name, new String[0], argumentBuilder);
	}

}
