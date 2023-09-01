/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bukkit.commandapi;

import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface ISuggestionProvider {

    static CompletableFuture<Suggestions> suggestVec3(String input, Collection<Coordinates> coordinates, SuggestionsBuilder builder, Predicate<String> filter) {
        List<String> list = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            for (Coordinates coords : coordinates) {
                String s = coords.x + " " + coords.y + " " + coords.z;
                if (filter.test(s)) {
                    list.add(coords.x);
                    list.add(coords.x + " " + coords.y);
                    list.add(s);
                }
            }
        } else {
            String[] args = input.split(" ");
            if (args.length == 1) {
                for (Coordinates coords : coordinates) {
                    String s1 = args[0] + " " + coords.y + " " + coords.z;
                    if (filter.test(s1)) {
                        list.add(args[0] + " " + coords.y);
                        list.add(s1);
                    }
                }
            } else if (args.length == 2) {
                for (Coordinates coords : coordinates) {
                    String s2 = args[0] + " " + args[1] + " " + coords.z;
                    if (filter.test(s2)) {
                        list.add(s2);
                    }
                }
            }
        }

        return suggest(list, builder);
    }

    static CompletableFuture<Suggestions> suggestVec2(String input, Collection<Coordinates> coordinates, SuggestionsBuilder builder, Predicate<String> filter) {
        List<String> list = new ArrayList<>();
        if (input == null || input.isEmpty()) {
            for (Coordinates coords : coordinates) {
                String s = coords.x + " " + coords.z;
                if (filter.test(s)) {
                    list.add(coords.x);
                    list.add(s);
                }
            }
        } else {
            String[] args = input.split(" ");
            if (args.length == 1) {
                for (Coordinates coords : coordinates) {
                    String s1 = args[0] + " " + coords.z;
                    if (filter.test(s1)) {
                        list.add(s1);
                    }
                }
            }
        }

        return suggest(list, builder);
    }

    static CompletableFuture<Suggestions> suggest(Iterable<String> suggestions, SuggestionsBuilder builder) {
        String prefix = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String suggestion : suggestions) {
            if (test(prefix, suggestion.toLowerCase(Locale.ROOT))) {
                builder.suggest(suggestion);
            }
        }
        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggest(Stream<String> suggestions, SuggestionsBuilder builder) {
        String input = builder.getRemaining().toLowerCase(Locale.ROOT);
        suggestions.filter((suggestion) -> test(input, suggestion.toLowerCase(Locale.ROOT))).forEach(builder::suggest);
        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggest(Iterable<String> suggestions, SuggestionsBuilder builder, String prefix) {
        String input = builder.getRemaining().toLowerCase(Locale.ROOT);
        for (String suggestion : suggestions) {
            String suggest = prefix + suggestion;
            if (test(input, suggest.toLowerCase(Locale.ROOT))) {
                builder.suggest(suggest);
            }
        }
        return builder.buildFuture();
    }

    static CompletableFuture<Suggestions> suggest(String[] suggestions, SuggestionsBuilder builder) {
        String prefix = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (String suggestion : suggestions) {
            if (test(prefix, suggestion.toLowerCase(Locale.ROOT))) {
                builder.suggest(suggestion);
            }
        }

        return builder.buildFuture();
    }

    static boolean test(String input, String suggestion) {
        for (int i = 0; !suggestion.startsWith(input, i); ++i) {
            i = suggestion.indexOf('_', i);
            if (i < 0) {
                return false;
            }
        }
        return true;
    }

    default Collection<String> getPlayerNames() {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    default Collection<String> getTargetedEntity() {
        return Collections.emptyList();
    }

    default Collection<Coordinates> getCoordinates() {
        return Collections.singleton(Coordinates.DEFAULT_GLOBAL);
    }

    class Coordinates {
        public static final Coordinates DEFAULT_LOCAL = new Coordinates("^", "^", "^");
        public static final Coordinates DEFAULT_GLOBAL = new Coordinates("~", "~", "~");
        public final String x;
        public final String y;
        public final String z;

        public Coordinates(String xIn, String yIn, String zIn) {
            this.x = xIn;
            this.y = yIn;
            this.z = zIn;
        }

        @Override public String toString() {
            return "Coordinates [x=" + x + ", y=" + y + ", z=" + z + "]";
        }

    }
}
