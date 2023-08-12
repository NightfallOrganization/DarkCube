/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PerkArgument implements ArgumentType<PerkSelector> {
    private static final DynamicCommandExceptionType INVALID_ENUM = Messages.INVALID_ENUM.newDynamicCommandExceptionType();
    private final Supplier<Perk[]> values;
    private final Function<Perk, String[]> toStringFunction;
    private final Function<String, Perk> fromStringFunction;
    private final Predicate<Perk> filter;
    private final WoolBattleBukkit woolbattle;
    private final boolean single;

    private PerkArgument(Supplier<Perk[]> perks, Predicate<Perk> filter, Function<Perk, String[]> toStringFunction, Function<String, Perk> fromStringFunction, boolean single, WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.filter = filter == null ? p -> true : filter;
        this.single = single;
        this.values = perks == null ? () -> woolbattle.perkRegistry().perks().values().toArray(new Perk[0]) : perks;
        this.toStringFunction = toStringFunction == null ? defaultToStringFunction() : toStringFunction;
        this.fromStringFunction = fromStringFunction == null ? defaultFromStringFunction() : fromStringFunction;
    }

    public static PerkArgument singlePerkArgument(WoolBattleBukkit woolbattle) {
        return new PerkArgument(null, null, null, null, true, woolbattle);
    }

    public static PerkArgument singlePerkArgument(Predicate<Perk> filter, WoolBattleBukkit woolbattle) {
        return new PerkArgument(null, filter, null, null, true, woolbattle);
    }

    public static PerkArgument perkArgument(WoolBattleBukkit woolbattle) {
        return new PerkArgument(null, null, null, null, false, woolbattle);
    }

    public static PerkArgument perkArgument(Predicate<Perk> filter, WoolBattleBukkit woolbattle) {
        return new PerkArgument(null, filter, null, null, false, woolbattle);
    }

    public static Collection<Perk> getPerkTypes(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, PerkSelector.class).select();
    }

    public static Perk getPerk(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, PerkSelector.class).selectOne();
    }

    private Perk[] values() {
        return Arrays.stream(values.get()).filter(filter).toArray(Perk[]::new);
    }

    @Override public PerkSelector parse(StringReader reader) throws CommandSyntaxException {
        int cursor = reader.getCursor();
        reader.skipWhitespace();
        String in = read(reader);
        if (!single) if (in.equals("*")) {
            return new PerkSelector(true, null, woolbattle);
        }
        Perk type = fromStringFunction.apply(in);
        if (type == null) {
            reader.setCursor(cursor);
            throw INVALID_ENUM.createWithContext(reader, in);
        }
        return new PerkSelector(false, type, woolbattle);
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> suggestions = new ArrayList<>();
        for (Perk t : values()) {
            suggestions.addAll(Arrays.asList(toStringFunction.apply(t)));
        }
        if (!single) suggestions.add("*");
        return ISuggestionProvider.suggest(suggestions, builder);
    }

    private String read(StringReader reader) {
        char c = ' ';
        StringBuilder b = new StringBuilder();
        while (reader.canRead() && reader.peek() != c) {
            b.append(reader.peek());
            reader.skip();
        }
        return b.toString();
    }

    private Function<String, Perk> defaultFromStringFunction() {
        final Map<String, Perk> map = new HashMap<>();
        for (Perk t : values()) {
            String[] arr = toStringFunction.apply(t);
            for (String s : arr) {
                if (map.containsKey(s)) {
                    System.out.println("[TeamArgument] Ambiguous name: " + s);
                } else {
                    map.put(s, t);
                }
            }
        }
        return map::get;
    }

    private Function<Perk, String[]> defaultToStringFunction() {
        final Map<Perk, String[]> map = new HashMap<>();
        for (Perk t : values()) {
            map.put(t, new String[]{t.perkName().getName()});
        }
        return map::get;
    }
}
