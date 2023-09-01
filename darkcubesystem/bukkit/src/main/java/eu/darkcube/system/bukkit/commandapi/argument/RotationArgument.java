/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi.argument;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class RotationArgument implements ArgumentType<ILocationArgument> {
    public static final SimpleCommandExceptionType ROTATION_INCOMPLETE = Messages.ROTATION_INCOMPLETE.newSimpleCommandExceptionType();
    private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "~-5 ~5");

    public static RotationArgument rotation() {
        return new RotationArgument();
    }

    public static ILocationArgument getRotation(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, ILocationArgument.class);
    }

    @Override public ILocationArgument parse(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();
        if (!reader.canRead()) {
            throw RotationArgument.ROTATION_INCOMPLETE.createWithContext(reader);
        }
        LocationPart yaw = LocationPart.parseDouble(reader, false);
        if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            LocationPart pitch = LocationPart.parseDouble(reader, false);
            return new LocationInput(yaw, pitch, new LocationPart(true, 0.0D));
        }
        reader.setCursor(i);
        throw RotationArgument.ROTATION_INCOMPLETE.createWithContext(reader);
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> source, SuggestionsBuilder builder) {
        if (!(source.getSource() instanceof ISuggestionProvider)) {
            return Suggestions.empty();
        }
        String s = builder.getRemaining();
        Collection<ISuggestionProvider.Coordinates> collection;
        if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.emptyList();
        } else {
            collection = ((ISuggestionProvider) source.getSource()).getCoordinates();
        }

        return ISuggestionProvider.suggestVec2(s, collection, builder, Commands.predicate(this::parse));
    }

    @Override public Collection<String> getExamples() {
        return RotationArgument.EXAMPLES;
    }
}
