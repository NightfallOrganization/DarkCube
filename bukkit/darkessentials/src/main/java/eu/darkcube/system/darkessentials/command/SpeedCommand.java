/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.Collection;
import java.util.List;

import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends DarkCommand {

    public SpeedCommand() {
        //@formatter:off
        super("speed",builder -> {
            builder.then(Commands.argument("amount", IntegerArgumentType.integer())
                    .executes(context -> executeCommand(context, IntegerArgumentType.getInteger(context, "amount"), List.of(context.getSource().asPlayer())))

                    .then(Commands.argument("player", EntityArgument.players()).executes(context -> executeCommand(context, IntegerArgumentType.getInteger(context, "amount"), EntityArgument.getPlayers(context, "player")))
                    )
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, int amount, @NotNull Collection<Player> players) throws CommandSyntaxException {

        for (Player player : players) {
            boolean isFlying = player.isFlying();
            User user = UserAPI.instance().user(player.getUniqueId());
            CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
            float speedValue = (float) amount / 10;

            if (amount <= 10) {
                if (sender.equals(player)) {
                    if (isFlying) {
                        user.sendMessage(Message.SPEED_FLY_SET, speedValue);
                        player.setFlySpeed(speedValue);
                    } else {
                        user.sendMessage(Message.SPEED_WALK_SET, speedValue);
                        player.setWalkSpeed(speedValue);
                    }
                } else {
                    if (isFlying) {
                        context.getSource().sendMessage(Message.SPEED_FLY_SETTED, player.getName(), speedValue);
                        user.sendMessage(Message.SPEED_FLY_SETTED_FROM, sender.getName(), speedValue);
                        player.setFlySpeed(speedValue);
                    } else {
                        context.getSource().sendMessage(Message.SPEED_WALK_SETTED, player.getName(), speedValue);
                        user.sendMessage(Message.SPEED_WALK_SETTED_FROM, sender.getName(), speedValue);
                        player.setWalkSpeed(speedValue);
                    }
                }
            } else {
                context.getSource().sendMessage(Message.SPEED_VALUE_TOO_MUCH);
            }
        }

        return 0;
    }

}
