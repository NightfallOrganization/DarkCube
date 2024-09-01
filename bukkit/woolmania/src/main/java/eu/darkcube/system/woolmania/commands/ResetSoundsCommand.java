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
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetSoundsCommand extends WoolManiaCommand {

    public ResetSoundsCommand() {
        //@formatter:off
        super("resetsounds",builder -> {
            builder.then(Commands.argument("player", EntityArgument.players())
                    .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player")))
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {

        for (Player player : players) {
            CommandSender sender = context.getSource().getSource().sender();
            User user = UserAPI.instance().user(player.getUniqueId());
            WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
            woolManiaPlayer.resetSounds();
            woolManiaPlayer.resetDefaultFarmingSound();

            if (sender.equals(player)) {
                context.getSource().sendMessage(Message.SOUND_RESET_OWN);
            } else {
                context.getSource().sendMessage(Message.SOUND_RESET, player.getName());
                user.sendMessage(Message.SOUND_RESETTED, sender.getName());
            }

        }
        return 0;
    }
}
