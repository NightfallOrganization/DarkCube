/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands.zenum;

import java.util.Collection;
import java.util.List;

import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.commands.WoolManiaCommand;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ZenumCommand extends WoolManiaCommand {
    public ZenumCommand() {
        // @formatter:off
        super("zenum",builder-> builder

                .then(Commands.argument("player", EntityArgument.players())
                        .executes(context -> firstExecuteCommand(context, EntityArgument.getPlayers(context, "player")))
                )

                .executes(context -> firstExecuteCommand(context, List.of(context.getSource().asPlayer())))

                .then(new SetZenumCommand().builder())
                .then(new AddZenumCommand().builder())
                .then(new RemoveZenumCommand().builder())
                .then(new SendZenumCommand().builder())
        );
        // @formatter:on
    }

    private static int firstExecuteCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {
        CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
        for (Player player : players) {
            int money = WoolMania.getStaticPlayer(player).getMoney();

            if (sender.equals(player)) {
                context.getSource().sendMessage(Message.ZENUM_OWN_YOURSELF, money);
            } else {
                context.getSource().sendMessage(Message.ZENUM_OWN, player.getName(), money);
            }
        }
        return 0;
    }
}