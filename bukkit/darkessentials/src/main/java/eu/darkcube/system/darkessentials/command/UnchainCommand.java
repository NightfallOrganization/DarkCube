/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.darkessentials.command;

import static eu.darkcube.system.darkessentials.listener.command.ChainCommandListener.chainKey;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.darkessentials.DarkCommand;
import eu.darkcube.system.darkessentials.util.Message;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class UnchainCommand extends DarkCommand {

    public UnchainCommand() {
        //@formatter:off
        super("unchain",builder -> {
            builder.then(Commands.argument("player", EntityArgument.player())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayer(context, "player")))
            );
        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, Player player) {
        User user = UserAPI.instance().user(player.getUniqueId());

        player.setWalkSpeed(0.2f);
        player.setFlySpeed(0.1f);
        player.removePotionEffect(PotionEffectType.BLINDNESS);
        player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 30f, 1f);
        user.sendMessage(Message.CHAIN_UNSETTED, context.getSource().getName());
        context.getSource().sendMessage(Message.CHAIN_UNSET, player.getName());
        user.metadata().remove(chainKey);

        return 0;
    }

}
