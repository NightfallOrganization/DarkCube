/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandCreateMap extends WBCommandExecutor {
    public CommandCreateMap(WoolBattleBukkit woolbattle) {
        super("createMap",
                b -> b.then(Commands.argument("map", StringArgumentType.string()).then(Commands.argument("size", MapSizeArgument.mapSize(woolbattle)).executes(ctx -> {
                    String mname = StringArgumentType.getString(ctx, "map");
                    MapSize mapSize = MapSizeArgument.mapSize(ctx, "size");
                    Map map = woolbattle.mapManager().getMap(mname, mapSize);
                    if (map != null) {
                        ctx.getSource().sendMessage(Component.text("Es gibt bereits eine Map mit dem Namen '" + mname + "'."));
                    } else {
                        map = woolbattle.mapManager().createMap(mname, mapSize);
                        ctx.getSource().sendMessage(Component.text("Du hast die Map " + map.getName() + " erstellt!"));
                    }
                    return 0;
                }))));
    }
}
