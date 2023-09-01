/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPlugin;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

import java.io.File;

public class CommandLoadWorld extends WBCommand {
    public CommandLoadWorld(WoolBattleBukkit woolbattle) {
        super("loadWorld", b -> b.then(Commands.argument("world", StringArgumentType.string()).executes(ctx -> {
            String worldName = StringArgumentType.getString(ctx, "world");
            if (!new File(woolbattle.getServer().getWorldContainer(), worldName).exists() && !new File(woolbattle
                    .getServer()
                    .getWorldContainer()
                    .getParent(), worldName).exists()) {
                ctx.getSource().sendMessage(Component.text("Diese Welt existiert nicht"));
                return 0;
            }
            VoidWorldPlugin.instance().loadWorld(worldName);
            ctx.getSource().sendMessage(Component.text("Welt geladen"));
            return 0;
        })));
    }
}
