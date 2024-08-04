package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import eu.darkcube.minigame.woolbattle.api.command.arguments.FromStringFunction;
import eu.darkcube.minigame.woolbattle.api.command.arguments.ToStringFunction;
import eu.darkcube.minigame.woolbattle.api.team.TeamConfiguration;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeamRegistry;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public class CommonTeamConfigurationArgument<T extends TeamConfiguration> implements ArgumentType<CommonTeamConfigurationArgument.Spec> {
    private static final DynamicCommandExceptionType TEAM_CONFIGURATION_NOT_FOUND = Messages.TEAM_CONFIGURATION_NOT_FOUND.newDynamicCommandExceptionType();
    private final Supplier<Collection<T>> values;
    private final ToStringFunction<T> toStringFunction;
    private final FromStringFunction<T> fromStringFunction;

    public CommonTeamConfigurationArgument(@Nullable CommonTeamRegistry teamRegistry, @Nullable Supplier<T[]> teams, @Nullable ToStringFunction<T> toStringFunction) {
        if (teams == null && teamRegistry == null) throw new NullPointerException("TeamSupplier and TeamRegistry can't both be null");
        this.values = teams == null ? () -> (Collection<T>) teamRegistry.teamConfigurations() : () -> List.of(teams.get());
        this.toStringFunction = toStringFunction == null ? ToStringFunction.of(T::key) : toStringFunction;
        this.fromStringFunction = FromStringFunction.of(this.toStringFunction, this.values);
    }

    public static CommonTeamConfigurationArgument<?> teamConfiguration(@NotNull CommonWoolBattleApi woolbattle) {
        return new CommonTeamConfigurationArgument<>(woolbattle.teamRegistry(), null, null);
    }

    public static <T extends TeamConfiguration> CommonTeamConfigurationArgument<?> teamConfiguration(@NotNull CommonWoolBattleApi woolbattle, @NotNull ToStringFunction<T> toStringFunction) {
        return new CommonTeamConfigurationArgument<>(woolbattle.teamRegistry(), null, toStringFunction);
    }

    public static <T extends TeamConfiguration> CommonTeamConfigurationArgument<?> teamConfiguration(@NotNull Supplier<@NotNull T[]> supplier) {
        return new CommonTeamConfigurationArgument<>(null, supplier, null);
    }

    public static <T extends TeamConfiguration> CommonTeamConfigurationArgument<T> teamConfiguration(@NotNull Supplier<@NotNull T[]> supplier, @NotNull ToStringFunction<T> toStringFunction) {
        return new CommonTeamConfigurationArgument<>(null, supplier, toStringFunction);
    }

    public static TeamConfiguration getTeamConfiguration(@NotNull CommandContext<?> ctx, @NotNull String name) throws CommandSyntaxException {
        return ctx.getArgument(name, Spec.class).parse(ctx);
    }

    @Override
    public Spec<T> parse(StringReader reader) throws CommandSyntaxException {
        var key = reader.readUnquotedString();
        return new Spec<>(fromStringFunction, key, new StringReader(reader));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(values.get().stream().map(t -> {
            try {
                return toStringFunction.toString(context, t);
            } catch (CommandSyntaxException e) {
                return new String[0];
            }
        }).flatMap(Arrays::stream), builder);
    }

    public static class Spec<T extends TeamConfiguration> {
        private final FromStringFunction<T> fromStringFunction;
        private final String key;
        private final StringReader reader;

        private Spec(FromStringFunction<T> fromStringFunction, String key, StringReader reader) {
            this.fromStringFunction = fromStringFunction;
            this.key = key;
            this.reader = reader;
        }

        public T parse(CommandContext<?> ctx) throws CommandSyntaxException {
            var type = fromStringFunction.fromString(ctx, this.key);
            if (type == null) throw TEAM_CONFIGURATION_NOT_FOUND.createWithContext(reader, this.key);
            return type;
        }
    }
}
