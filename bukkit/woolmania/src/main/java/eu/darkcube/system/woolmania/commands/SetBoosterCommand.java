/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import static eu.darkcube.system.woolmania.util.message.Message.*;

import java.util.Collection;
import java.util.List;

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
import eu.darkcube.system.woolmania.util.message.Message;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetBoosterCommand extends WoolManiaCommand {

    public SetBoosterCommand() {
        //@formatter:off
        super("setbooster",builder -> {
            builder.then(Commands.argument("amount", IntegerArgumentType.integer(0))
                    .executes(context -> executeCommand(context, List.of(context.getSource().asPlayer()), IntegerArgumentType.getInteger(context, "amount")))

                    .then(Commands.argument("player", EntityArgument.players())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player"), IntegerArgumentType.getInteger(context, "amount")))
                    )
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players, int amount) throws CommandSyntaxException {

        for (Player player : players) {
            CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
            User user = UserAPI.instance().user(player.getUniqueId());
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
            woolManiaPlayer.setPrivateBooster(amount, player);

            if (amount >= 2) {
                amountCheck(sender, player, amount, user, context, BOOSTER_SET_OWN, BOOSTER_SET, BOOSTER_SETTED);
            } else {
                amountCheck(sender, player, amount, user, context, BOOSTER_SET_OWN_NONE, BOOSTER_SET_NONE, BOOSTER_SETTED_NONE);
            }
        }
        return 0;
    }

    private static void amountCheck(CommandSender sender, Player player, int amount, User user, CommandContext<CommandSource> context, Message messageOWN, Message messageSET, Message messageSETTED) {
        if (sender.equals(player)) {
            context.getSource().sendMessage(messageOWN, amount);
        } else {
            context.getSource().sendMessage(messageSET, player.getName(), amount);
            user.sendMessage(messageSETTED, sender.getName(), amount);
        }
    }

}
