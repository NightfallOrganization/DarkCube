/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.user.WBUser;

public class CommandFix extends WBCommand {
    public CommandFix(WoolBattleBukkit woolbattle) {
        super("fix", b -> b.requires(s -> woolbattle.ingame().enabled()).executes(ctx -> {
            WBUser user = WBUser.getUser(ctx.getSource().asPlayer());
            woolbattle.ingame().playerUtil().setPlayerItems(user);
            return 0;
        }));
    }
}
