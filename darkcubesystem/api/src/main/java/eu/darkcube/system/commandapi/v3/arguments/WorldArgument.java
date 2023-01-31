/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3.arguments;

import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Messages;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class WorldArgument implements ArgumentType<World> {

	private static final DynamicCommandExceptionType INVALID_WORLD =
			Messages.INVALID_WORLD.newDynamicCommandExceptionType();

	private WorldArgument() {
	}

	public static WorldArgument world() {
		return new WorldArgument();
	}

	public static World getWorld(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, World.class);
	}

	@Override
	public World parse(StringReader reader) throws CommandSyntaxException {
		String worldname = reader.readString();
		for (World world : Bukkit.getWorlds()) {
			if (world.getName().equals(worldname)) {
				return world;
			}
		}
		throw WorldArgument.INVALID_WORLD.createWithContext(reader, worldname);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
			SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(Bukkit.getWorlds().stream().map(World::getName),
				builder);
	}
}