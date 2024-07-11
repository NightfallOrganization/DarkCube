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
import org.bukkit.inventory.Inventory;

public class InvseeCommand extends DarkCommand {

    public InvseeCommand() {
        //@formatter:off
        super("invsee",builder -> {
            builder.then(Commands.argument("player", EntityArgument.player())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayer(context, "player")))
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, Player player) throws CommandSyntaxException {

        Inventory playerInventory = player.getInventory();
        context.getSource().asPlayer().openInventory(playerInventory);

        return 0;
    }

}
