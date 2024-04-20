package eu.darkcube.minigame.woolbattle.common.command.arguments;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.CommonGameManager;
import eu.darkcube.minigame.woolbattle.common.util.translation.Messages;
import eu.darkcube.system.commandapi.ISuggestionProvider;
import eu.darkcube.system.libs.com.mojang.brigadier.StringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.ArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.Suggestions;
import eu.darkcube.system.libs.com.mojang.brigadier.suggestion.SuggestionsBuilder;

public class CommonGameArgument implements ArgumentType<CommonGame> {
    private static final DynamicCommandExceptionType GAME_NOT_FOUND = Messages.GAME_NOT_FOUND.newDynamicCommandExceptionType();
    private final CommonGameManager gameManager;

    private CommonGameArgument(CommonGameManager gameManager) {
        this.gameManager = gameManager;
    }

    public static CommonGameArgument gameArgument(CommonWoolBattleApi woolbattle) {
        return new CommonGameArgument(woolbattle.games());
    }

    public static CommonGame game(CommandContext<?> context, String name) {
        return context.getArgument(name, CommonGame.class);
    }

    @Override
    public CommonGame parse(StringReader reader) throws CommandSyntaxException {
        var idString = reader.readString();
        UUID id;
        try {
            id = UUID.fromString(idString);
        } catch (IllegalArgumentException exception) {
            throw GAME_NOT_FOUND.createWithContext(reader, idString);
        }
        var game = gameManager.game(id);
        if (game == null) {
            throw GAME_NOT_FOUND.createWithContext(reader, id.toString());
        }
        return game;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return ISuggestionProvider.suggest(gameManager.games().stream().map(CommonGame::id).map(UUID::toString), builder);
    }
}
