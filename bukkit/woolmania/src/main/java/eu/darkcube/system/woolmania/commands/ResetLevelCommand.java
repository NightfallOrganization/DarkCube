/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import java.util.Collection;
import java.util.List;

import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.handler.LevelXPHandler;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetLevelCommand extends WoolManiaCommand {

    public ResetLevelCommand() {
        //@formatter:off
        super("resetlevel",builder -> {
            builder.executes(context -> executeCommand(context, List.of(context.getSource().asPlayer())))

                    .then(Commands.argument("player", EntityArgument.players())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player")))
                    );

        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {

        for (Player player : players) {
            CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
            User user = UserAPI.instance().user(player.getUniqueId());
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
            woolManiaPlayer.setLevel(1);
            woolManiaPlayer.setXP(0);

            if (sender.equals(player)) {
                context.getSource().sendMessage(Message.LEVEL_RESET_OWN);
            } else {
                context.getSource().sendMessage(Message.LEVEL_RESET, player.getName());
                user.sendMessage(Message.LEVEL_RESETTED, sender.getName());
            }

        }
        return 0;
    }

}
