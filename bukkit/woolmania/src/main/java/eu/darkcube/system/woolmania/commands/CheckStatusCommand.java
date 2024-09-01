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
import eu.darkcube.system.woolmania.enums.hall.Halls;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CheckStatusCommand extends WoolManiaCommand {

    public CheckStatusCommand() {
        //@formatter:off
        super("checkstatus",builder -> {
            builder.then(Commands.argument("player", EntityArgument.players())
                    .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player")))
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {

        for (Player player : players) {
            CommandSender sender = context.getSource().getSource().sender();
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);

            if (sender.equals(player)) {
                woolManiaPlayer.lockAllHalls();
                // woolManiaPlayer.unlockHall(Halls.HALL1);

                for (Halls hall : Halls.values()) {
                    if (woolManiaPlayer.isHallUnlocked(hall)) {
                        player.sendMessage("§7Unlocked Hall: §e" + hall.name());
                    } else {
                        player.sendMessage("§7Locked Hall: §e" + hall.name());
                    }
                }

            } else {
                woolManiaPlayer.unlockHall(Halls.HALL1);
                player.sendMessage("Deactivated Ability");
            }

        }
        return 0;
    }
}
