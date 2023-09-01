/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.arguments;

import eu.darkcube.system.bukkit.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC;
import eu.darkcube.system.lobbysystem.util.Message;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class GamemodeConnectorArgument implements ArgumentType<ConnectorNPC> {
	private static final DynamicCommandExceptionType NPC_NOT_FOUND =
			Message.CONNECTOR_NPC_NOT_FOUND.newDynamicCommandExceptionType();

	@Override
	public ConnectorNPC parse(StringReader stringReader) throws CommandSyntaxException {
		String key = stringReader.readString();
		ConnectorNPC npc = ConnectorNPC.get(key);
		if (npc == null)
			throw NPC_NOT_FOUND.createWithContext(stringReader, key);
		return npc;
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
			SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(npcStream().map(ConnectorNPC::key), builder);
	}

	protected Stream<ConnectorNPC> npcStream() {
		return ConnectorNPC.npcStream();
	}
}
