/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UUIDArgument implements ArgumentType<UUID> {

    private static final DynamicCommandExceptionType INVALID_UUID = Messages.INVALID_UUID.newDynamicCommandExceptionType();

    public static UUIDArgument uuid() {
        return new UUIDArgument();
    }

    public static UUID getUUID(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, UUID.class);
    }

    @Override public UUID parse(StringReader r) throws CommandSyntaxException {
        String s = r.readUnquotedString();
        try {
            return UUID.fromString(s);
        } catch (Exception ex) {
            throw INVALID_UUID.createWithContext(r, s);
        }
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).map(UUID::toString), builder);
    }
}
