/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.StringArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandCreateMap extends WBCommandExecutor {
    public CommandCreateMap() {
        super("createMap",
                b -> b.then(Commands.argument("map", StringArgument.string()).then(Commands.argument("size", MapSizeArgument.mapSize()).executes(ctx -> {
                    String mname = StringArgument.getString(ctx, "map");
                    MapSize mapSize = MapSizeArgument.mapSize(ctx, "size");
                    Map map = WoolBattle.instance().mapManager().getMap(mname);
                    if (map != null) {
                        ctx.getSource().sendMessage(Component.text(
                                "Es gibt bereits eine Map mit dem Namen '" + mname + "'."));
                    } else {
                        map = WoolBattle.instance().mapManager().createMap(mname, mapSize);
                        ctx.getSource().sendMessage(
                                Component.text("Du hast die Map " + map.getName() + " erstellt!"));
                    }
                    return 0;
                }))));
    }
}
