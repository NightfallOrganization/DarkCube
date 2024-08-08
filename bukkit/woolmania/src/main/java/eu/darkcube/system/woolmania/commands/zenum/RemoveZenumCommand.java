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

public class RemoveZenumCommand extends WoolManiaCommand {

    public RemoveZenumCommand() {
        // @formatter:off
        super("remove",builder -> {
            builder.then(Commands.argument("player", EntityArgument.players())
                    .then(Commands.argument("amount", IntegerArgumentType.integer())
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
            if (sender.equals(player)) {
                WoolMania.getStaticPlayer(player).removeMoney(amount);
                user.sendMessage(Message.ZENUM_ADD_YOURSELF, amount);
            } else {
                WoolMania.getStaticPlayer(player).removeMoney(amount);
                context.getSource().sendMessage(Message.ZENUM_ADD_OTHER, player.getName(), amount);
                user.sendMessage(Message.ZENUM_ADDED, amount, context.getSource().getName());
            }
        }
        return 0;
    }

}
