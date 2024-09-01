/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands.texturepack;

import java.util.Collection;

import eu.darkcube.system.bukkit.commandapi.BukkitCommandExecutor;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.commands.WoolManiaCommand;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TexturePackCommand extends WoolManiaCommand {
    public static final NamespacedKey texturepackKey = new NamespacedKey(WoolMania.getInstance(), "texturepack_off");

    public TexturePackCommand() {
        // @formatter:off
        super("texturepack",builder-> builder
                .then(new TexturePackOnCommand().builder().requires(source->source.getSource().hasPermission("woolmania.texturepack.on")))
                .then(new TexturePackOffCommand().builder().requires(source->source.getSource().hasPermission("woolmania.texturepack.off")))
        );
        // @formatter:on
    }

    private static int firstExecuteCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {
        CommandSender sender = ((BukkitCommandExecutor) context.getSource().getSource()).sender();
        for (Player player : players) {
            // int money = WoolMania.getStaticPlayer(player).getMoney();

            // if (sender.equals(player)) {
            //     context.getSource().sendMessage(Message.ZENUM_OWN_YOURSELF, money);
            // } else {
            //     context.getSource().sendMessage(Message.ZENUM_OWN, player.getName(), money);
            // }
        }
        return 0;
    }
}