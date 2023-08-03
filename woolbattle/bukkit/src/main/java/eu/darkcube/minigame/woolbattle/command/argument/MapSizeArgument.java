/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class MapSizeArgument implements ArgumentType<MapSize> {
    private static final DynamicCommandExceptionType TYPE_INVALID_SIZE = Message.INVALID_MAP_SIZE.newDynamicCommandExceptionType();
    private final WoolBattle woolbattle;
    private final Predicate<MapSize> validate;

    public MapSizeArgument(WoolBattle woolbattle, Predicate<MapSize> validate) {
        this.woolbattle = woolbattle;
        this.validate = validate;
    }

    public static MapSizeArgument mapSize(WoolBattle woolbattle) {
        return mapSize(woolbattle, m -> true);
    }

    public static MapSizeArgument mapSize(WoolBattle woolbattle, Predicate<MapSize> validate) {
        return new MapSizeArgument(woolbattle, validate);
    }

    public static <S> MapSize mapSize(CommandContext<S> context, String name) {
        return context.getArgument(name, MapSize.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(woolbattle.knownMapSizes().stream().map(MapSize::toString).distinct(), builder);
    }

    @Override
    public MapSize parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        String s = reader.readString();
        String[] a = s.split("x");
        if (a.length != 2) {
            reader.setCursor(cursor);
            throw TYPE_INVALID_SIZE.createWithContext(reader, s);
        }
        int teams;
        int teamSize;
        try {
            teams = Integer.parseInt(a[0]);
            teamSize = Integer.parseInt(a[1]);
        } catch (NumberFormatException ex) {
            reader.setCursor(cursor);
            throw TYPE_INVALID_SIZE.createWithContext(reader, s);
        }
        MapSize mapSize = new MapSize(teams, teamSize);
        if (!validate.test(mapSize)) {
            reader.setCursor(cursor);
            throw TYPE_INVALID_SIZE.createWithContext(reader, s);
        }
        return mapSize;
    }
}
