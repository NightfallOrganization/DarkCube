/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands.zenum;

import java.util.Collection;

import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.commands.WoolManiaCommand;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SendZenumCommand extends WoolManiaCommand {

    public SendZenumCommand() {
        // @formatter:off
        super("send",builder -> {
            builder.then(Commands.argument("player", EntityArgument.players())
                    .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "amount")))
                    )
            );
        });
        // @formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players, int amount) throws CommandSyntaxException {

        for (Player player : players) {
            User user = UserAPI.instance().user(player.getUniqueId());
            CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
            Player contextPlayer = context.getSource().asPlayer();
            int money = WoolMania.getStaticPlayer(contextPlayer).getMoney();

            if (sender.equals(player)) {
                if (amount <= money) {
                    WoolMania.getStaticPlayer(contextPlayer).removeMoney(amount, player);
                    WoolMania.getStaticPlayer(player).addMoney(amount, player);
                    user.sendMessage(Message.ZENUM_SEND_YOURSELF, amount);
                } else {
                    context.getSource().sendMessage(Message.ZENUM_NOT_ENOUGH);
                }
            } else {
                if (amount <= money) {
                    WoolMania.getStaticPlayer(contextPlayer).removeMoney(amount, player);
                    WoolMania.getStaticPlayer(player).addMoney(amount, player);
                    context.getSource().sendMessage(Message.ZENUM_SEND_OTHER, player.getName(), amount);
                    user.sendMessage(Message.ZENUM_SENDED, amount, context.getSource().getName());
                } else {
                    context.getSource().sendMessage(Message.ZENUM_NOT_ENOUGH);
                }
            }
        }
        return 0;
    }

}
