/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class MapSizeArgument implements ArgumentType<MapSize> {
    private static final SimpleCommandExceptionType TYPE_INVALID_SIZE = Message.INVALID_MAP_SIZE.newSimpleCommandExceptionType();

    private MapSizeArgument() {
    }

    public static MapSizeArgument mapSize() {
        return new MapSizeArgument();
    }

    public static MapSize mapSize(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, MapSize.class);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String[] suggestions = WoolBattle.instance().mapManager().getMaps().stream().map(Map::size).map(MapSize::toString).toArray(String[]::new);
        return ISuggestionProvider.suggest(suggestions, builder);
    }

    @Override
    public MapSize parse(StringReader reader) throws CommandSyntaxException {
        String s = reader.readString();
        String[] a = s.split("x");
        if (a.length != 2) throw TYPE_INVALID_SIZE.create();
        int teams;
        int teamSize;
        try {
            teams = Integer.parseInt(a[0]);
            teamSize = Integer.parseInt(a[1]);
        } catch (NumberFormatException ex) {
            throw TYPE_INVALID_SIZE.create();
        }
        return new MapSize(teams, teamSize);
    }
}
