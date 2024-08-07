/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;

public class ForceMapCommand extends PServer {

    public ForceMapCommand(WoolBattleBukkit woolbattle) {
        super("forcemap", new String[0], b -> b.then(Commands.argument("map", MapArgument.mapArgument(woolbattle)).executes(context -> {
            Map map = MapArgument.getMap(context, "map");
            woolbattle.gameData().forceMap(map);
            context.getSource().sendMessage(Message.WOOLBATTLE_FORCEMAP, map.getName());
            return 0;
        })));
    }

}
