package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.maps;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.map;
import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.mapArgument;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;

import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommand;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.maps.map.SetIconCommand;

public class MapCommand extends WoolBattleCommand {
    public static final String MAP_NAME = "map";

    public MapCommand(CommonWoolBattleApi woolbattle) {
        // @formatter:off
        super("map", b -> b
                .then(argument(MAP_NAME, mapArgument())
                        .executes(ctx -> {
                            var map = map(ctx, MAP_NAME);
                            System.out.println(map);
                            return 0;
                        })
                        .then(new SetIconCommand(woolbattle).builder())
                )
        );
        // @formatter:on
    }
}
