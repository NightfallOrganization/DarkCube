/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandLoad extends WBCommandExecutor {
    public CommandLoad() {
        super("load", b -> {
            b.then(Commands.argument("map", MapArgument.mapArgument()).executes(ctx -> {
                Map map = MapArgument.getMap(ctx, "map");
                WoolBattle.instance().mapLoader().loadMap(map).thenRun(() -> ctx.getSource().sendMessage(Component.text("Map loaded"))).exceptionally(t -> {
                    t.printStackTrace();
                    return null;
                });
                return 0;
            }));
        });
    }
}
