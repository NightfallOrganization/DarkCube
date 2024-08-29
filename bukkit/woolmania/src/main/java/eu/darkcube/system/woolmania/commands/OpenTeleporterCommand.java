/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import java.util.Collection;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.woolmania.WoolMania;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenTeleporterCommand extends WoolManiaCommand {

    public OpenTeleporterCommand() {
        //@formatter:off
        super("openteleporter",builder -> {
            builder.then(Commands.argument("player", EntityArgument.players())
                    .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player")))
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {

        for (Player player : players) {
            CommandSender sender = context.getSource().getSource().sender();

            if (sender.equals(player)) {
                WoolMania.getInstance().getTeleportInventory().openInventory(context.getSource().asPlayer());
            } else {
                WoolMania.getInstance().getTeleportInventory().openInventory(player);
            }

        }
        return 0;
    }
}
