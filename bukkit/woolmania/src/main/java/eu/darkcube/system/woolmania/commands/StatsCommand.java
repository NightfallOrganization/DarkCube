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
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.player.LevelXPHandler;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatsCommand extends WoolManiaCommand {

    public StatsCommand() {
        //@formatter:off
        super("stats",builder -> {
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
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
            int level = woolManiaPlayer.getLevel();
            double xp = woolManiaPlayer.getXP();
            int xpRequired = LevelXPHandler.calculateXPRequiredForNextLevel(level);
            int money = woolManiaPlayer.getMoney();
            int farmedBlocks = woolManiaPlayer.getFarmedBlocks();

            sender.sendMessage("");
            sender.sendMessage("§b" + player.getName() + " §7Stats:");
            sender.sendMessage("");
            sender.sendMessage("§7Level: §b" + level);
            sender.sendMessage("§7XP: §b" + xp + "§7/§b" + xpRequired);
            sender.sendMessage("§7Zenum: §b" + money);
            sender.sendMessage("§7Farmed: §b" + farmedBlocks);
            sender.sendMessage("");

        }
        return 0;
    }

}
