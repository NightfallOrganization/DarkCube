/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments;

import static eu.darkcube.system.commandapi.ISuggestionProvider.suggest;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import eu.darkcube.minigame.woolbattle.api.command.CommandSource;
import eu.darkcube.minigame.woolbattle.api.team.Team;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class CommonTeamArgument implements ArgumentType<CommonTeamArgument.TeamSelector> {
    private static final String GAME_PREFIX = "game-";
    private static final DynamicCommandExceptionType TEAM_NOT_FOUND = Messages.TEAM_CONFIGURATION_NOT_FOUND.newDynamicCommandExceptionType();
    private static final DynamicCommandExceptionType GAME_NOT_FOUND = Messages.GAME_NOT_FOUND.newDynamicCommandExceptionType();
    private static final SimpleCommandExceptionType NOT_A_PLAYER = Messages.NO_PLAYER.newSimpleCommandExceptionType();
    private static final SimpleCommandExceptionType NOT_IN_A_GAME = Messages.NOT_IN_A_GAME.newSimpleCommandExceptionType();
    private final CommonWoolBattleApi api;
    private final boolean allowSpectator;

    private CommonTeamArgument(CommonWoolBattleApi api, boolean allowSpectator) {
        this.api = api;
        this.allowSpectator = allowSpectator;
    }

    public static CommonTeamArgument team(CommonWoolBattleApi api, boolean allowSpectator) {
        return new CommonTeamArgument(api, allowSpectator);
    }

    public static CommonTeam team(CommandContext<?> ctx, String name) throws CommandSyntaxException {
        var source = (CommandSource) ctx.getSource();
        var selector = ctx.getArgument(name, TeamSelector.class);
        return Parser.resolve(selector, source);
    }

    @Override
    public TeamSelector parse(StringReader reader) throws CommandSyntaxException {
        var parser = new Parser(reader);
        return parser.parse();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var source = (CommandSource) context.getSource();
        var reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        var parser = new Parser(reader);
        try {
            parser.parse();
        } catch (CommandSyntaxException _) {
        }
        return parser.suggestions.apply(builder, source);
    }

    private class Parser {
        private final StringReader reader;
        private BiFunction<SuggestionsBuilder, CommandSource, CompletableFuture<Suggestions>> suggestions;
        private @Nullable CommonGame game;
        private @Nullable String teamName;
        private @Nullable UUID teamId;

        public Parser(StringReader reader) {
            this.reader = reader;
        }

        public TeamSelector parse() throws CommandSyntaxException {
            this.suggestions = this::suggestGamesOrLocalTeam;
            var cursor = reader.getCursor();
            var first = reader.readUnquotedString();
            var teamInput = first;
            if (first.startsWith(GAME_PREFIX)) {
                var gameIdString = first.substring(GAME_PREFIX.length());
                UUID gameId;
                try {
                    gameId = UUID.fromString(gameIdString);
                } catch (IllegalArgumentException _) {
                    reader.setCursor(cursor);
                    throw GAME_NOT_FOUND.createWithContext(reader, gameIdString);
                }
                game = api.games().game(gameId);
                if (game == null) {
                    reader.setCursor(cursor);
                    throw GAME_NOT_FOUND.createWithContext(reader, gameId);
                }
                this.suggestions = this::suggestGameSpecificTeam;
                teamInput = reader.readUnquotedString();
            }
            try {
                teamId = UUID.fromString(teamInput);
            } catch (IllegalArgumentException _) {
                teamName = teamInput;
            }
            return new TeamSelector(game, teamName, teamId, allowSpectator);
        }

        public static CommonTeam resolve(TeamSelector selector, CommandSource source) throws CommandSyntaxException {
            if (selector.game != null) {
                return resolveTeam(selector.game, selector);
            } else if (source.source() instanceof CommonWBUser user) {
                var game = user.game();
                if (game == null) {
                    throw NOT_IN_A_GAME.create();
                }
                return resolveTeam(game, selector);
            } else {
                throw NOT_A_PLAYER.create();
            }
        }

        private static CommonTeam resolveTeam(CommonGame game, TeamSelector selector) throws CommandSyntaxException {
            Stream<CommonTeam> teams;
            String stringed;
            if (selector.teamName != null) {
                stringed = Objects.requireNonNull(selector.teamName);
                teams = selectTeams(selector.allowSpectator, game).filter(team -> team.key().equals(selector.teamName));
            } else {
                stringed = Objects.requireNonNull(selector.teamUUID).toString();
                teams = selectTeams(selector.allowSpectator, game).filter(team -> team.uniqueId().equals(selector.teamUUID));
            }
            return teams.findFirst().orElseThrow(() -> TEAM_NOT_FOUND.create(stringed));
        }

        private CompletableFuture<Suggestions> suggestGameSpecificTeam(SuggestionsBuilder builder, CommandSource source) {
            return suggest(teams(game), builder);
        }

        private CompletableFuture<Suggestions> suggestGamesOrLocalTeam(SuggestionsBuilder builder, CommandSource source) {
            if (source.source() instanceof CommonWBUser user) {
                var game = user.game();
                suggest(api.games().games().stream().filter(g -> g != game).map(id -> GAME_PREFIX + id), builder);
                suggest(teams(game), builder);
            } else {
                suggest(api.games().games().stream().map(CommonGame::id).map(id -> GAME_PREFIX + id), builder);
            }
            return builder.buildFuture();
        }

        private static Stream<CommonTeam> selectTeams(boolean allowSpectator, CommonGame game) {
            return (allowSpectator ? game.teamManager().teams() : game.teamManager().playingTeams()).stream();
        }

        private Stream<String> teams(@Nullable CommonGame game) {
            if (game == null) return Stream.empty();
            return selectTeams(allowSpectator, game).map(Team::key);
        }
    }

    public record TeamSelector(@Nullable CommonGame game, @Nullable String teamName, @Nullable UUID teamUUID, boolean allowSpectator) {
    }
}
