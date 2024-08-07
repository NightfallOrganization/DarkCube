/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.util.Collection;

import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;

public class VanishCommand extends DarkCommand {

    public VanishCommand() {
        super("vanish", builder -> builder.executes(context -> {

            //                Player player = context.getSource().asPlayer().getPlayer();
            //                context.getSource().asPlayer().hidePlayer();
            //
            //                if (isFlying) {
            //                    context.getSource().asPlayer().setAllowFlight(false);
            //                    context.getSource().asPlayer().setFlying(false);
            //                    context.getSource().sendMessage(Message.FLY_OFF);
            //                } else {
            //                    context.getSource().asPlayer().setAllowFlight(true);
            //                    context.getSource().asPlayer().setFlying(true);
            //                    context.getSource().sendMessage(Message.FLY_ON);
            //                }

            return 0;

        }).then(Commands.argument("players", EntityArgument.players()).executes(context -> {

            Collection<Player> players = EntityArgument.getPlayers(context, "players");
            boolean isFlying = context.getSource().asPlayer().isFlying();

            for (Player player : players) {
                User user = UserAPI.instance().user(player.getUniqueId());
                String playerName = player.getName();
                String playerSenderName = context.getSource().asPlayer().getName();

                if (isFlying) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    user.sendMessage(Message.FLY_SET_OFF, playerSenderName);
                    context.getSource().sendMessage(Message.FLY_SETTED_OFF, playerName);
                } else {
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    user.sendMessage(Message.FLY_SET_ON, playerSenderName);
                    context.getSource().sendMessage(Message.FLY_SETTED_ON, playerName);
                }
            }

            return 0;

        })));
    }

}
