package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.map.CommonMap;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

class CommonMapArgument implements ArgumentType<CommonMap> {
    private static final DynamicCommandExceptionType INVALID_MAP_SIZE = Messages.INVALID_MAP_SIZE.newDynamicCommandExceptionType();
    private static final DynamicCommandExceptionType INVALID_MAP = Messages.INVALID_MAP.newDynamicCommandExceptionType();
    private final CommonWoolBattleApi woolbattle;

    public CommonMapArgument(CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
    }

    public static CommonMapArgument map(CommonWoolBattleApi woolbattle) {
        return new CommonMapArgument(woolbattle);
    }

    public static CommonMap getMap(CommandContext<?> ctx, String name) {
        return ctx.getArgument(name, CommonMap.class);
    }

    @Override
    public CommonMap parse(StringReader reader) throws CommandSyntaxException {
        return new Parser(reader).parse();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var reader = new StringReader(builder.getInput());
        reader.setCursor(builder.getStart());
        var parser = new Parser(reader);
        try {
            parser.parse();
        } catch (CommandSyntaxException ignored) {
        }
        return parser.suggest(builder);
    }

    private class Parser {
        private final StringReader reader;
        private MapSize size;
        private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestionHandler;

        public Parser(StringReader reader) {
            this.reader = reader;
        }

        private CommonMap parse() throws CommandSyntaxException {
            this.suggestionHandler = this::suggestMapSize;
            var cursor = reader.getCursor();
            var mapSizeString = reader.readUnquotedString();
            try {
                size = MapSize.parseString(mapSizeString);
            } catch (IllegalArgumentException _) {
                reader.setCursor(cursor);
                throw INVALID_MAP_SIZE.createWithContext(reader, mapSizeString);
            }
            reader.skipWhitespace();
            this.suggestionHandler = this::suggestMapName;
            cursor = reader.getCursor();
            var mapNameString = reader.readString();
            var maps = woolbattle.mapManager().maps(size);
            for (var map : maps) {
                if (map.name().equals(mapNameString)) {
                    return map;
                }
            }
            reader.setCursor(cursor);
            throw INVALID_MAP.createWithContext(reader, mapNameString);
        }

        private CompletableFuture<Suggestions> suggest(SuggestionsBuilder builder) {
            return suggestionHandler.apply(builder.createOffset(reader.getCursor()));
        }

        private CompletableFuture<Suggestions> suggestMapSize(SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(woolbattle.mapManager().maps().stream().map(Map::size).distinct().map(MapSize::toString), builder);
        }

        private CompletableFuture<Suggestions> suggestMapName(SuggestionsBuilder builder) {
            return ISuggestionProvider.suggest(woolbattle.mapManager().maps(size).stream().map(Map::name), builder);
        }
    }
}
