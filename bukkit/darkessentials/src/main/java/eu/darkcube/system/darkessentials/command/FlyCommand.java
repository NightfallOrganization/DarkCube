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
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends DarkCommand {

    public FlyCommand() {
        //@formatter:off
        super("fly", new String[]{"flying"}, builder -> {
            builder.executes(context -> executeCommand(context, List.of(context.getSource().asPlayer())))

                    .then(Commands.argument("player", EntityArgument.players())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player")))
                    );

        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) {

        for (Player player : players) {
            boolean isFlying = player.isFlying();
            User user = UserAPI.instance().user(player.getUniqueId());
            String playerName = player.getName();
            String playerSenderName = context.getSource().getName();

            player.setAllowFlight(!isFlying);
            player.setFlying(!isFlying);

            CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
            if (sender.equals(player)) {
                if (isFlying) {
                    user.sendMessage(Message.FLY_OFF);
                } else {
                    user.sendMessage(Message.FLY_ON);
                }
            } else {
                if (isFlying) {
                    user.sendMessage(Message.FLY_SET_OFF, playerSenderName);
                    context.getSource().sendMessage(Message.FLY_SETTED_OFF, playerName);
                } else {
                    user.sendMessage(Message.FLY_SET_ON, playerSenderName);
                    context.getSource().sendMessage(Message.FLY_SETTED_ON, playerName);
                }
            }
        }
        return 0;
    }

}
