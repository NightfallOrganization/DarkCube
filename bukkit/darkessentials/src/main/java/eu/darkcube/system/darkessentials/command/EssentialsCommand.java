/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.function.Consumer;

public class EssentialsCommand extends CommandExecutor {

	public EssentialsCommand(String name, String[] aliases,
			Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		super("essentials", name, generateAliases("essentials", aliases), argumentBuilder);
	}

	public EssentialsCommand(String name,
			Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
		this(name, new String[0], argumentBuilder);
	}

	private static String[] generateAliases(String name, String... aliases) {
		String[] otherAliases = new String[1 + aliases.length * 2];
		otherAliases[0] = "d_" + name;
		for (int i = 0; i < aliases.length; i++) {
			otherAliases[i * 2 + 1] = aliases[i];
			otherAliases[i * 2 + 2] = "d_" + aliases[i];
		}
		return otherAliases;
	}
}
