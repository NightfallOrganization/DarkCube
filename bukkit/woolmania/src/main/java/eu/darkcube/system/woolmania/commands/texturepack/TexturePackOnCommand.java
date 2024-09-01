/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands.texturepack;

import java.util.Collection;
import java.util.List;

import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.bukkit.commandapi.argument.EntityArgument;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.woolmania.commands.WoolManiaCommand;
import eu.darkcube.system.woolmania.util.player.ResourcePackUtil;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

public class TexturePackOnCommand extends WoolManiaCommand {

    public TexturePackOnCommand() {
        //@formatter:off
        super("on",builder -> {
            builder.executes(context -> executeCommand(context, List.of(context.getSource().asPlayer())))

                    .then(Commands.argument("player", EntityArgument.players())
                            .executes(context -> executeCommand(context, EntityArgument.getPlayers(context, "player")))
                    );

        });
        //@formatter:on
    }

    private static int executeCommand(CommandContext<CommandSource> context, @NotNull Collection<Player> players) throws CommandSyntaxException {

        for (Player player : players) {
            PersistentDataContainer data = player.getPersistentDataContainer();
            data.remove(TexturePackCommand.texturepackKey);
            player.sendMessage("§7TexturePack: §bON");
            ResourcePackUtil.loadTexturePack(player);
        }
        return 0;
    }

}