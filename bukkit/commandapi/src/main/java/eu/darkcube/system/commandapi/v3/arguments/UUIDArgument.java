/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.v3.arguments;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Message;

public class UUIDArgument implements ArgumentType<UUID> {

	private static final DynamicCommandExceptionType INVALID_UUID =
			Message.INVALID_UUID.newDynamicCommandExceptionType();

	public static UUIDArgument uuid() {
		return new UUIDArgument();
	}

	public static UUID getUUID(CommandContext<CommandSource> context, String name) {
		return context.getArgument(name, UUID.class);
	}

	@Override
	public UUID parse(StringReader r) throws CommandSyntaxException {
		String s = r.readUnquotedString();
		try {
			UUID uuid = UUID.fromString(s);
			return uuid;
		} catch (Exception ex) {
			throw INVALID_UUID.createWithContext(r, s);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context,
			SuggestionsBuilder builder) {
		return ISuggestionProvider.suggest(
				Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).map(UUID::toString),
				builder);
	}
}
