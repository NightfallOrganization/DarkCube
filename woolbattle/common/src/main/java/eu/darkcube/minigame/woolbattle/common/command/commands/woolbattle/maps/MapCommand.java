/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.commands.woolbattle.maps;

import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.map;
import static eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments.mapArgument;
import static eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands.argument;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;

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
                            // @formatter:on
                            var map = map(ctx, MAP_NAME);
                            var source = ctx.getSource();
                            source.sendMessage(text("Map: " + map.name() + "-" + map.size()));
                            source.sendMessage(text("Enabled: " + map.enabled()));
                            source.sendMessage(text("Icon: " + map.icon().serialize()));
                            return 0;
                            // @formatter:off
                        })
                        .then(new SetIconCommand(woolbattle).builder())
                )
        );
        // @formatter:on
    }
}
