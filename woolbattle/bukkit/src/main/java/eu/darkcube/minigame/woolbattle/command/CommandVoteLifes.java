/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;

public class CommandVoteLifes extends WBCommandExecutor {
    public CommandVoteLifes() {
        super("votelifes", new String[]{"vl", "vlifes"}, b -> b.then(
                Commands.argument("lifes", IntegerArgumentType.integer(3, 30)).executes(ctx -> {
                    WBUser user = WBUser.getUser(ctx.getSource().asPlayer());
                    int lifes = IntegerArgumentType.getInteger(ctx, "lifes");
                    WoolBattle.instance().lobby().VOTES_LIFES.put(user, lifes);
                    user.user().sendMessage(Message.VOTED_LIFES, lifes);
                    return 0;
                })));
    }
}
