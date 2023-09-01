/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;

public class ReviveCommand extends PServer {
    public ReviveCommand(WoolBattleBukkit woolbattle) {
        super("revive", new String[0], b -> b.then(Commands.argument("player", EntityArgument.player()).executes(context -> {
            WBUser user = WBUser.getUser(EntityArgument.getPlayer(context, "player"));
            if (woolbattle.ingame().playerUtil().revive(user)) {
                context.getSource().sendMessage(Message.WOOLBATTLE_REVIVED_PLAYER, user.getTeamPlayerName());
            } else {
                context.getSource().sendMessage(Message.WOOLBATTLE_REVIVE_NO_TEAM_FOUND, user.getTeamPlayerName());
            }
            return 0;
        })));
    }

}
