/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.command;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CommandTimer extends Command {

    public CommandTimer() {
        super("miners", "timer", new String[0], b -> b.then(Commands
                .argument("time", IntegerArgumentType.integer(1))
                .executes(context -> setTime(IntegerArgumentType.getInteger(context, "time"), context.getSource().asPlayer()))));
    }

    public static int setTime(int secs, Player sender) {
        switch (Miners.getGamephase()) {
            case 0 -> {
                if (!Miners.getLobbyPhase().getTimer().isRunning()) Miners.getLobbyPhase().getTimer().start(secs * 1000L);
                else Miners.getLobbyPhase().getTimer().setEndTime(System.currentTimeMillis() + secs * 1000L);
            }
            case 1 -> {
                if (Miners.getMiningPhase().getTimer().isRunning())
                    Miners.getMiningPhase().getTimer().setEndTime(System.currentTimeMillis() + secs * 1000L);
            }
            case 2 -> {
                Bukkit.getWorld(Miners.PVP_WORLD_NAME).getWorldBorder().setSize(0, secs);
                if (Miners.getPVPPhase().getTimer().isRunning())
                    Miners.getPVPPhase().getTimer().setEndTime(System.currentTimeMillis() + secs * 1000L);
            }
            default -> {
            }
        }
        Miners.sendTranslatedMessage(sender, Message.COMMAND_TIMER_CHANGED, secs);
        return 0;
    }

}
