/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapArgument implements ArgumentType<MapArgument.MapSpec> {
    private static final DynamicCommandExceptionType INVALID_ENUM = Messages.INVALID_ENUM.newDynamicCommandExceptionType();
    private final WoolBattleBukkit woolbattle;
    private final ToStringFunction toStringFunction;
    private final FromStringFunction fromStringFunction;

    public MapArgument(WoolBattleBukkit woolbattle, ToStringFunction toStringFunction) {
        this.woolbattle = woolbattle;
        this.toStringFunction = toStringFunction;
        this.fromStringFunction = FromStringFunction.of(this::maps, toStringFunction);
    }

    public static MapArgument mapArgument(WoolBattleBukkit woolbattle, ToStringFunction toStringFunction) {
        return new MapArgument(woolbattle, toStringFunction);
    }

    public static MapArgument mapArgument(WoolBattleBukkit woolbattle, MapSize mapSize) {
        return mapArgument(woolbattle, ToStringFunction.function(mapSize));
    }

    public static MapArgument mapArgument(WoolBattleBukkit woolbattle) {
        return mapArgument(woolbattle, ToStringFunction.function(ctx -> woolbattle.gameData().mapSize()));
    }

    public static Map getMap(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, MapSpec.class).parse(context);
    }

    @Override public MapSpec parse(StringReader reader) throws CommandSyntaxException {
        StringReader clone = new StringReader(reader);
        return new MapSpec(reader.readString(), clone);
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> suggestions = new ArrayList<>();
        for (Map map : maps()) {
            suggestions.addAll(Arrays.asList(toStringFunction.toString(context, map)));
        }
        return ISuggestionProvider.suggest(suggestions, builder);
    }

    private Map[] maps() {
        return woolbattle.mapManager().getMaps().toArray(new Map[0]);
    }

    public interface ToStringFunction {

        static ToStringFunction function(MapSize mapSize) {
            return new ToStringFunction() {
                @Override public <S> String[] toString(CommandContext<S> context, Map map) {
                    if (!map.size().equals(mapSize)) return new String[0];
                    return new String[]{map.getName()};
                }
            };
        }

        static ToStringFunction function(Function<CommandContext<?>, MapSize> mapSizeSupplier) {
            return new ToStringFunction() {
                @Override public <S> String[] toString(CommandContext<S> context, Map map) {
                    if (!map.size().equals(mapSizeSupplier.apply(context))) return new String[0];
                    return new String[]{map.getName()};
                }
            };
        }

        <S> String[] toString(CommandContext<S> context, Map map);
    }

    public interface FromStringFunction {
        static FromStringFunction of(Supplier<Map[]> maps, ToStringFunction f) {
            return new FromStringFunction() {
                @Override public <S> Map fromString(CommandContext<S> context, String string) {
                    Map[] a = maps.get();
                    for (Map map : a) {
                        String[] sa = f.toString(context, map);
                        if (Arrays.asList(sa).contains(string)) {
                            return map;
                        }
                    }
                    return null;
                }
            };
        }

        <S> Map fromString(CommandContext<S> context, String string);
    }

    public class MapSpec {
        private final String mapName;
        private final StringReader reader;

        private MapSpec(String mapName, StringReader reader) {
            this.mapName = mapName;
            this.reader = reader;
        }

        public <S> Map parse(CommandContext<S> context) throws CommandSyntaxException {
            Map type = fromStringFunction.fromString(context, mapName);
            if (type == null) throw INVALID_ENUM.createWithContext(reader, mapName);
            return type;
        }
    }
}
