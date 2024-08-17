/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.minigame.woolbattle.api.util.MinMaxBounds;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class CommonEntityArgument implements ArgumentType<CommonEntityArgument.EntitySelector> {

    private final CommonWoolBattleApi woolbattle;
    private final boolean player;
    private final boolean single;

    public CommonEntityArgument(CommonWoolBattleApi woolbattle, boolean player, boolean single) {
        this.woolbattle = woolbattle;
        this.player = player;
        this.single = single;
    }

    @Override
    public EntitySelector parse(StringReader reader) throws CommandSyntaxException {
        return new Parser(reader).parse();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        var parser = new Parser(reader);
        try {
            parser.parse();
        } catch (CommandSyntaxException _) {
        }
        return parser.suggest(builder);
    }

    private class Parser {
        private final StringReader reader;
        private int cursorStart;
        private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestionsHandler;

        public Parser(StringReader reader) {
            this.reader = reader;
        }

        private EntitySelector parse() throws CommandSyntaxException {

        }

        public EntitySelector build() {
            
        }

        private CompletableFuture<Suggestions> suggestNameOrSelector(SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(woolbattle.games().games().stream().flatMap(g -> g.users().stream()).map(CommonWBUser::playerName), builder);
        }

        private CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder) {
            return suggestionsHandler.apply(builder.createOffset(reader.getCursor()));
        }
    }

    private static class EntitySelector {
        private final int limit;
        private final boolean includeNonPlayers;
        private final boolean currentWordOnly;
        private final Predicate<Entity> filter;
        private final MinMaxBounds.FloatBound distance;
        private final Function<Position, Position> positionGetter;
        private final BoundingBox boundingBox;
        private final BiConsumer<Position, List<? extends Entity>> sorter;
        private final boolean self;
        private final String username;
        private final UUID uuid;
        private final EntityType type;

        public EntitySelector(int limit, boolean includeNonPlayers, boolean currentWordOnly, Predicate<Entity> filter, MinMaxBounds.FloatBound distance, Function<Position, Position> positionGetter, BoundingBox boundingBox, BiConsumer<Position, List<? extends Entity>> sorter, boolean self, String username, UUID uuid, EntityType type) {
            this.limit = limit;
            this.includeNonPlayers = includeNonPlayers;
            this.currentWordOnly = currentWordOnly;
            this.filter = filter;
            this.distance = distance;
            this.positionGetter = positionGetter;
            this.boundingBox = boundingBox;
            this.sorter = sorter;
            this.self = self;
            this.username = username;
            this.uuid = uuid;
            this.type = type;
        }
    }
}
