package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle;

import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.literal;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

import java.util.stream.Collectors;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;

public class GameCommand extends WoolBattleCommand {
    public GameCommand(CommonWoolBattleApi woolbattle) {
        super("game", b -> b.then(literal("list").executes(ctx -> {
            var source = ctx.getSource();
            var games = woolbattle.games().games();
            source.sendMessage(text("Games (" + games.size() + ")"));
            for (var game : games) {
                source.sendMessage(text(" - Game (" + game.id() + "), Size: " + game.mapSize()));
            }
            return 0;
        })).then(argument("game", WoolBattleArguments.gameArgument(woolbattle)).executes(ctx -> {
            var source = ctx.getSource();
            var game = WoolBattleArguments.game(ctx, "game");
            source.sendMessage(text("Game: " + game.id()));
            source.sendMessage(text("Size: " + game.mapSize()));
            source.sendMessage(text("Map: " + game.map().name()));
            source.sendMessage(text("State: " + game.gameState()));
            source.sendMessage(text("Users: " + game.users().stream().map(WBUser::playerName).collect(Collectors.joining(", ", "[", "]"))));
            return 0;
        })));
    }
}
