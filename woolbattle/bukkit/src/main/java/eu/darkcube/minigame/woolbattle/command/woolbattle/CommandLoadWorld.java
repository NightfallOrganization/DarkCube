/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.voidworldplugin.VoidWorldPlugin;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.StringArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

import java.io.File;

public class CommandLoadWorld extends WBCommandExecutor {
    public CommandLoadWorld(WoolBattle woolbattle) {
        super("loadWorld",
                b -> b.then(Commands.argument("world", StringArgument.string()).executes(ctx -> {
                    String worldName = StringArgument.getString(ctx, "world");
                    if (!new File(woolbattle.getServer().getWorldContainer(), worldName).exists() && !new File(woolbattle.getServer().getWorldContainer().getParent(), worldName).exists()) {
                        ctx.getSource().sendMessage(Component.text("Diese Welt existiert nicht"));
                        return 0;
                    }
                    VoidWorldPlugin.instance().loadWorld(worldName);
                    ctx.getSource().sendMessage(Component.text("Welt geladen"));
                    return 0;
                })));
    }
}
