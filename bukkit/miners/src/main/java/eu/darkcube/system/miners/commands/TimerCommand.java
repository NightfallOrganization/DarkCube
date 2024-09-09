/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.commands;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.miners.enums.Sounds;
import eu.darkcube.system.miners.gamephase.lobbyphase.LobbyTimer;
import eu.darkcube.system.miners.utils.message.Message;
import org.bukkit.entity.Player;

public class TimerCommand extends MinersCommand {

    public TimerCommand() {
        //@formatter:off
        super("timer",builder -> {
            builder.then(Commands.argument("amount", IntegerArgumentType.integer())
                    .executes(context -> executeCommand(context, IntegerArgumentType.getInteger(context, "amount")))
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, int amount) throws CommandSyntaxException {

        Player player = context.getSource().asPlayer();
        LobbyTimer.lobbyTime = amount;
        Sounds.SOUND_SET.playSound(player);
        context.getSource().sendMessage(Message.TIME_SET, amount);

        return 0;
    }

}