package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
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

class CommonMapSizeArgument implements ArgumentType<MapSize> {
    private static final DynamicCommandExceptionType INVALID_MAP_SIZE = Messages.INVALID_MAP_SIZE.newDynamicCommandExceptionType();
    private static final Collection<String> EXAMPLES = List.of("2x1", "2x40", "4x2");

    private final @NotNull CommonWoolBattleApi woolbattle;
    private final @NotNull Predicate<@NotNull MapSize> validate;

    private CommonMapSizeArgument(@NotNull CommonWoolBattleApi woolbattle, @NotNull Predicate<@NotNull MapSize> validate) {
        this.woolbattle = woolbattle;
        this.validate = validate;
    }

    public static CommonMapSizeArgument mapSize(@NotNull CommonWoolBattleApi woolbattle) {
        return mapSize(woolbattle, _ -> true);
    }

    public static CommonMapSizeArgument mapSize(@NotNull CommonWoolBattleApi woolbattle, @NotNull Predicate<@NotNull MapSize> validate) {
        return new CommonMapSizeArgument(woolbattle, validate);
    }

    public static MapSize getMapSize(CommandContext<?> ctx, String name) {
        return ctx.getArgument(name, MapSize.class);
    }

    @Override
    public MapSize parse(StringReader reader) throws CommandSyntaxException {
        var cursor = reader.getCursor();
        var string = reader.readUnquotedString();
        try {
            var size = MapSize.parseString(string);
            if (!validate.test(size)) throw new IllegalArgumentException();
            return size;
        } catch (IllegalArgumentException e) {
            reader.setCursor(cursor);
            throw INVALID_MAP_SIZE.createWithContext(reader, string);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(woolbattle.mapManager().maps().stream().map(Map::size).distinct().map(MapSize::toString), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}