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
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class GodCommand extends DarkCommand {

    public GodCommand() {
        //@formatter:off
        super("god", new String[]{"godmode"}, builder -> {
            builder.executes(context -> executeCommand(context, List.of(context.getSource().asPlayer())))

                    .then(Commands.argument("player", EntityArgument.players())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player")))
                    );

        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {
        // TODO
        // for (Player player : players) {
        //     boolean isGodmode = player.isGodmode();
        //     User user = UserAPI.instance().user(player.getUniqueId());
        //
        //     Key key = Key.key("test", "test_this");
        //     user.metadata().set(key, "whatever");
        //
        //     user.metadata().get(key);
        //
        //     String playerName = player.getName();
        //     String playerSenderName = context.getSource().getName();
        //
        //     player.setAllowFlight(!isGodmode);
        //     player.setFlying(!isGodmode);
        //
        //     CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
        //     if (sender.equals(player)) {
        //         if (isGodmode) {
        //             user.sendMessage(Message.GOD_OFF);
        //         } else {
        //             user.sendMessage(Message.GOD_ON);
        //         }
        //     } else {
        //         if (isGodmode) {
        //             user.sendMessage(Message.GOD_SET_OFF, playerSenderName);
        //             context.getSource().sendMessage(Message.GOD_SETTED_OFF, playerName);
        //         } else {
        //             user.sendMessage(Message.GOD_SET_ON, playerSenderName);
        //             context.getSource().sendMessage(Message.GOD_SETTED_ON, playerName);
        //         }
        //     }
        // }
        return 0;
    }

}
