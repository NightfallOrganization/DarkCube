/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.argument;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.team.TeamType;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TeamTypeArgument implements ArgumentType<TeamTypeArgument.TeamTypeSpec> {
    private static final DynamicCommandExceptionType INVALID_ENUM = Messages.INVALID_ENUM.newDynamicCommandExceptionType();
    private final Supplier<TeamType[]> values;
    private final ToStringFunction toStringFunction;
    private final FromStringFunction fromStringFunction;

    private TeamTypeArgument(WoolBattleBukkit woolbattle, Supplier<TeamType[]> teams, ToStringFunction toStringFunction) {
        this.values = teams == null ? () -> woolbattle.teamManager().teamTypes().toArray(new TeamType[0]) : teams;
        this.toStringFunction = toStringFunction == null ? ToStringFunction.function() : toStringFunction;
        this.fromStringFunction = FromStringFunction.of(this.values, this.toStringFunction);
    }

    public static TeamTypeArgument teamTypeArgument(WoolBattleBukkit woolbattle) {
        return new TeamTypeArgument(woolbattle, null, null);
    }

    public static TeamTypeArgument teamTypeArgument(WoolBattleBukkit woolbattle, MapSize mapSize) {
        return new TeamTypeArgument(woolbattle, () -> woolbattle.teamManager().teamTypes(mapSize).toArray(new TeamType[0]), null);
    }

    public static TeamTypeArgument teamTypeArgument(WoolBattleBukkit woolbattle, ToStringFunction toStringFunction) {
        return new TeamTypeArgument(woolbattle, null, toStringFunction);
    }

    public static TeamType teamType(CommandContext<CommandSource> context, String name) throws CommandSyntaxException {
        return context.getArgument(name, TeamTypeSpec.class).parse(context);
    }

    @Override
    public TeamTypeSpec parse(StringReader reader) throws CommandSyntaxException {
        StringReader clone = new StringReader(reader);
        String in = reader.readString();
        return new TeamTypeSpec(in, clone);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        List<String> suggestions = new ArrayList<>();
        for (TeamType t : values.get()) {
            suggestions.addAll(Arrays.asList(toStringFunction.toString(context, t)));
        }
        return ISuggestionProvider.suggest(suggestions, builder);
    }

    public interface ToStringFunction {
        static ToStringFunction function() {
            return new ToStringFunction() {
                @Override
                public <S> String[] toString(CommandContext<S> context, TeamType teamType) {
                    return new String[]{teamType.getDisplayNameKey()};
                }
            };
        }

        <S> String[] toString(CommandContext<S> context, TeamType teamType);
    }

    public interface FromStringFunction {
        static FromStringFunction of(Supplier<TeamType[]> teams, ToStringFunction f) {
            return new FromStringFunction() {
                @Override
                public <S> TeamType fromString(CommandContext<S> context, String string) {
                    TeamType[] a = teams.get();
                    for (TeamType teamType : a) {
                        String[] sa = f.toString(context, teamType);
                        if (Arrays.asList(sa).contains(string)) {
                            return teamType;
                        }
                    }
                    return null;
                }
            };
        }

        <S> TeamType fromString(CommandContext<S> context, String string);
    }

    public class TeamTypeSpec {
        private final String displayNameKey;
        private final StringReader reader;

        private TeamTypeSpec(String displayNameKey, StringReader reader) {
            this.displayNameKey = displayNameKey;
            this.reader = reader;
        }

        public <S> TeamType parse(CommandContext<S> context) throws CommandSyntaxException {
            TeamType type = fromStringFunction.fromString(context, displayNameKey);
            if (type == null) throw INVALID_ENUM.createWithContext(reader, displayNameKey);
            return type;
        }
    }
}
