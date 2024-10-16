/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.commandapi.util.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class TeamArgument implements ArgumentType<Team> {
    private static final DynamicCommandExceptionType INVALID_ENUM = Messages.INVALID_ENUM.newDynamicCommandExceptionType();
    private final WoolBattleBukkit woolbattle;
    private final boolean spectator;

    private TeamArgument(WoolBattleBukkit woolbattle) {
        this(woolbattle, false);
    }

    private TeamArgument(WoolBattleBukkit woolbattle, boolean spectator) {
        this.woolbattle = woolbattle;
        this.spectator = spectator;
    }

    public static Team team(CommandContext<?> context, String name) {
        return context.getArgument(name, Team.class);
    }

    public static TeamArgument teamArgument(WoolBattleBukkit woolbattle) {
        return new TeamArgument(woolbattle);
    }

    public static TeamArgument teamArgumentSpectator(WoolBattleBukkit woolbattle) {
        return new TeamArgument(woolbattle, true);
    }

    @Override public Team parse(StringReader reader) throws CommandSyntaxException {
        var dnk = reader.readString();
        var team = teams()
                .filter(t -> !t.isSpectator() || spectator)
                .filter(t -> t.getType().getDisplayNameKey().equals(dnk))
                .findFirst()
                .orElse(null);
        if (team != null) return team;
        throw INVALID_ENUM.createWithContext(reader, dnk);
    }

    private Stream<Team> teams() {
        return Stream.concat(Stream.of(woolbattle.teamManager().getSpectator()), woolbattle.teamManager().getTeams().stream());
    }

    @Override public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var suggestions = teams().filter(t -> {
            if (!spectator) return !t.isSpectator();
            return true;
        }).map(t -> t.getType().getDisplayNameKey());
        return ISuggestionProvider.suggest(suggestions, builder);
    }
}
