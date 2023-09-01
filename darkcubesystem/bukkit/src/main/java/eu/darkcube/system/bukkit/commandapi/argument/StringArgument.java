/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

@Deprecated(forRemoval = true) public class StringArgument implements ArgumentType<String> {

    private final String[] suggestions;

    private StringArgument(String... suggestions) {
        this.suggestions = suggestions;
    }

    public static StringArgument string(String... suggestions) {
        return new StringArgument(suggestions);
    }

    public static String getString(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override public String parse(StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(this.suggestions, builder);
    }
}
