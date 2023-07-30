/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.system.commandapi.v3.ISuggestionProvider;
import eu.darkcube.system.commandapi.v3.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class TeamArgument implements ArgumentType<Team> {
    private static final DynamicCommandExceptionType INVALID_ENUM =
            Messages.INVALID_ENUM.newDynamicCommandExceptionType();
    private final WoolBattle woolbattle;
    private final boolean spectator;

    private TeamArgument(WoolBattle woolbattle) {
        this(woolbattle, false);
    }

    private TeamArgument(WoolBattle woolbattle, boolean spectator) {
        this.woolbattle = woolbattle;
        this.spectator = spectator;
    }

    public static Team team(CommandContext<?> context, String name) {
        return context.getArgument(name, Team.class);
    }

    public static TeamArgument teamArgument(WoolBattle woolbattle) {
        return new TeamArgument(woolbattle);
    }

    public static TeamArgument teamArgumentSpectator(WoolBattle woolbattle) {
        return new TeamArgument(woolbattle, true);
    }

    @Override
    public Team parse(StringReader reader) throws CommandSyntaxException {
        String dnk = reader.readString();
        for (Team team : woolbattle.teamManager().getTeams()) {
            if (!spectator && team.isSpectator()) continue;
            if (team.getType().getDisplayNameKey().equals(dnk)) return team;
        }
        throw INVALID_ENUM.createWithContext(reader, dnk);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        Stream<String> suggestions = woolbattle.teamManager().getTeams().stream().filter(t -> {
            if (!spectator) return !t.isSpectator();
            return true;
        }).map(t -> t.getType().getDisplayNameKey());
        return ISuggestionProvider.suggest(suggestions, builder);
    }
}
