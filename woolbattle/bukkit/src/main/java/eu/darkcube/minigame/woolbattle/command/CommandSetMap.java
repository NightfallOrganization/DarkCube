/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scoreboard.ScoreboardHelper;
import eu.darkcube.system.commandapi.v3.Commands;

public class CommandSetMap extends WBCommandExecutor {
    public CommandSetMap(WoolBattleBukkit woolbattle) {
        super("setMap", b -> b
                .then(Commands.argument("map", MapArgument.mapArgument(woolbattle)).executes(ctx -> {
                    Map map = MapArgument.getMap(ctx, "map");
                    woolbattle.gameData().forceMap(map);
                    if (woolbattle.lobby().enabled()) {
                        WBUser.onlineUsers().forEach(u -> ScoreboardHelper.setMap(woolbattle, u));
                    } else if (woolbattle.ingame().enabled()) {
                        woolbattle.gameData().forceMap(map);
                        for (WBUser user : WBUser.onlineUsers()) {
                            user.getBukkitEntity().teleport(user.getTeam().getSpawn());
                        }
                    }
                    ctx.getSource().sendMessage(Message.MAP_CHANGED, map.getName());
                    return 0;
                })));
    }
}
