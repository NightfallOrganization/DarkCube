/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.FloatArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand extends DarkCommand {

    public SpeedCommand() {
        //@formatter:off
        super("speed",builder -> {
            builder.then(Commands.argument("amount", FloatArgumentType.floatArg(0, 10))
                    .executes(context -> executeCommand(context, FloatArgumentType.getFloat(context, "amount"), List.of(context.getSource().asPlayer())))

                    .then(Commands.argument("player", EntityArgument.players()).executes(context -> executeCommand(context, FloatArgumentType.getFloat(context, "amount"), EntityArgument.getPlayers(context, "player")))
                    )
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, float amount, @NotNull Collection<Player> players) {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.#####", otherSymbols);

        for (Player player : players) {
            boolean isFlying = player.isFlying();
            float defaultSpeed = isFlying ? 0.1f : 0.2f;
            float maxSpeed = 1F;
            float playerSpeed = defaultSpeed + ((amount - 1F) / 9F) * (maxSpeed - defaultSpeed);
            User user = UserAPI.instance().user(player.getUniqueId());
            CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();

            if (sender.equals(player)) {
                if (isFlying) {
                    user.sendMessage(Message.SPEED_FLY_SET, df.format(amount));
                    player.setFlySpeed(playerSpeed);
                } else {
                    user.sendMessage(Message.SPEED_WALK_SET, df.format(amount));
                    player.setWalkSpeed(playerSpeed);
                }
            } else {
                if (isFlying) {
                    context.getSource().sendMessage(Message.SPEED_FLY_SETTED, player.getName(), df.format(amount));
                    user.sendMessage(Message.SPEED_FLY_SETTED_FROM, sender.getName(), df.format(amount));
                    player.setFlySpeed(playerSpeed);
                } else {
                    context.getSource().sendMessage(Message.SPEED_WALK_SETTED, player.getName(), df.format(amount));
                    user.sendMessage(Message.SPEED_WALK_SETTED_FROM, sender.getName(), df.format(amount));
                    player.setWalkSpeed(playerSpeed);
                }
            }
        }

        return 0;
    }

}
