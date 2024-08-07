/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.bukkit.commandapi.Commands;

public class CommandSetMap extends WBCommand {
    public CommandSetMap(WoolBattleBukkit woolbattle) {
        super("setMap", b -> b.then(Commands.argument("map", MapArgument.mapArgument(woolbattle)).executes(ctx -> {
            Map map = MapArgument.getMap(ctx, "map");
            Map currentMap = woolbattle.gameData().map();
            if (map != currentMap) {
                woolbattle.gameData().forceMap(map);
            }
            ctx.getSource().sendMessage(Message.MAP_CHANGED, map.getName());
            return 0;
        })));
    }
}
