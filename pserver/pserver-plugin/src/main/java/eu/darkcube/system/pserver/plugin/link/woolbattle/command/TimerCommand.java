/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.link.woolbattle.command;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServer;

public class TimerCommand extends PServer {

    public TimerCommand(WoolBattleBukkit woolbattle) {
        super("timer", new String[0], b -> b.then(Commands.argument("timer", IntegerArgumentType.integer(0, 10000)).executes(context -> {
            int timer = IntegerArgumentType.getInteger(context, "timer");
            CommandSource source = context.getSource();
            woolbattle.lobby().setOverrideTimer(timer <= 1 ? 2 : timer * 20);
            source.sendMessage(Message.WOOLBATTLE_TIMER, timer);
            return 0;
        })));
    }

}
