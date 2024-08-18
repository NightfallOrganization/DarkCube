/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments;

import static eu.darkcube.minigame.woolbattle.common.command.arguments.entity.TranslatableWrapper.translatable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.arguments.entity.EntitySelector;
import eu.darkcube.minigame.woolbattle.common.command.arguments.entity.EntitySelectorParser;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class CommonEntityArgument implements ArgumentType<EntitySelector> {
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_ENTITY = new SimpleCommandExceptionType(translatable("argument.entity.toomany"));
    public static final SimpleCommandExceptionType ERROR_NOT_SINGLE_PLAYER = new SimpleCommandExceptionType(translatable("argument.player.toomany"));
    public static final SimpleCommandExceptionType ERROR_ONLY_PLAYERS_ALLOWED = new SimpleCommandExceptionType(translatable("argument.player.entities"));
    public static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(translatable("argument.entity.notfound.entity"));
    public static final SimpleCommandExceptionType NO_PLAYERS_FOUND = new SimpleCommandExceptionType(translatable("argument.entity.notfound.player"));
    public static final SimpleCommandExceptionType ERROR_SELECTORS_NOT_ALLOWED = new SimpleCommandExceptionType(translatable("argument.entity.selector.not_allowed"));
    private final CommonWoolBattleApi woolbattle;
    private final boolean playerOnly;
    private final boolean single;

    public CommonEntityArgument(CommonWoolBattleApi woolbattle, boolean playerOnly, boolean single) {
        this.woolbattle = woolbattle;
        this.playerOnly = playerOnly;
        this.single = single;
    }

    @Override
    public EntitySelector parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader, true);
    }

    public <S> EntitySelector parse(StringReader stringreader, S s0) throws CommandSyntaxException {
        return this.parse(stringreader, EntitySelectorParser.allowSelectors(s0));
    }

    private EntitySelector parse(StringReader reader, boolean allowAtSelectors) throws CommandSyntaxException {
        // CraftBukkit start
        return this.parse(reader, allowAtSelectors, false);
    }

    public EntitySelector parse(StringReader stringreader, boolean flag, boolean overridePermissions) throws CommandSyntaxException {
        // CraftBukkit end
        boolean flag1 = false;
        EntitySelectorParser argumentparserselector = new EntitySelectorParser(stringreader, flag);
        EntitySelector entityselector = argumentparserselector.parse(overridePermissions); // CraftBukkit

        if (entityselector.getMaxResults() > 1 && this.single) {
            if (this.playerOnly) {
                stringreader.setCursor(0);
                throw ERROR_NOT_SINGLE_PLAYER.createWithContext(stringreader);
            } else {
                stringreader.setCursor(0);
                throw ERROR_NOT_SINGLE_ENTITY.createWithContext(stringreader);
            }
        } else if (entityselector.includesEntities() && this.playerOnly && !entityselector.isSelfSelector()) {
            stringreader.setCursor(0);
            throw ERROR_ONLY_PLAYERS_ALLOWED.createWithContext(stringreader);
        } else {
            return entityselector;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if (context.getSource() instanceof ISuggestionProvider suggestionProvider) {
            var reader = new StringReader(builder.getInput());
            reader.setCursor(builder.getStart());
            var parser = new EntitySelectorParser(reader, true);

            try {
                parser.parse();
            } catch (CommandSyntaxException ignored) {
            }

            return parser.fillSuggestions(builder, sbuilder -> {
                var collection = suggestionProvider.getPlayerNames();
                var iterable = this.playerOnly ? collection : concat(collection, suggestionProvider.getTargetedEntity());
                ISuggestionProvider.suggest(iterable, sbuilder);
            });
        }
        return Suggestions.empty();
    }

    private <T> Collection<T> concat(Collection<T> c1, Collection<T> c2) {
        var l = new ArrayList<>(c1);
        l.addAll(c2);
        return l;
    }
}
