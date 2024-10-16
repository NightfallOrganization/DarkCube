/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;

public class CommandTimer extends WBCommand {

    public CommandTimer(WoolBattleBukkit woolbattle) {
        super("timer", b -> b.then(Commands.argument("time", IntegerArgumentType.integer(0)).executes(ctx -> {
            int time = IntegerArgumentType.getInteger(ctx, "time");
            woolbattle.lobby().setOverrideTimer(time * 20);
            ctx.getSource().sendMessage(Message.TIMER_CHANGED, time);
            return 0;
        })));
    }
}
