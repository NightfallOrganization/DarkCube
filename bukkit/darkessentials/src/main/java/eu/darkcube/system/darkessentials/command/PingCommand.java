/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PingCommand extends DarkCommand {

    public PingCommand() {
        //@formatter:off
        super("ping", builder -> {
            builder.executes(context -> executeCommand(context, context.getSource().asPlayer()))

                    .then(Commands.argument("player", EntityArgument.player())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayer(context, "player")))
                    );

        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, Player player) throws CommandSyntaxException {
        CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();

        if (sender.equals(player)) {
            try {
                Object handle = sender.getClass().getMethod("getHandle").invoke(sender);
                int ping = (int) handle.getClass().getField("ping").get(handle);
                context.getSource().sendMessage(Component.text("§7Ping: §e" + ping));
            } catch (Exception e) {
                context.getSource().sendMessage(Component.text("§cPing error: Contact Administrators"));
            }
        } else {
            try {
                Object handle = player.getClass().getMethod("getHandle").invoke(player);
                int ping = (int) handle.getClass().getField("ping").get(handle);
                sender.sendMessage("§7" + player.getName() + "'s ping: §e" + ping);
            } catch (Exception e) {
                player.sendMessage("§cPing error: Contact Administrators");
            }
        }

        return 0;
    }

}
