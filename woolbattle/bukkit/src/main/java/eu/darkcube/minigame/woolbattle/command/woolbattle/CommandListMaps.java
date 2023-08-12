/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

import java.util.Collection;

public class CommandListMaps extends WBCommandExecutor {
    public CommandListMaps(WoolBattleBukkit woolbattle) {
        super("listMaps", b -> b
                .executes(ctx -> listMaps(ctx, woolbattle.mapManager().getMaps()))
                .then(Commands
                        .argument("mapSize", MapSizeArgument.mapSize(woolbattle))
                        .executes(ctx -> listMaps(ctx, woolbattle.mapManager().getMaps(MapSizeArgument.mapSize(ctx, "mapSize"))))));
    }

    private static int listMaps(CommandContext<CommandSource> ctx, Collection<? extends Map> maps) {
        Component c = Component.empty();
        if (maps.isEmpty()) {
            c = Component.text("Es sind keine Maps erstellt");
        } else {
            for (Map map : maps) {
                c = c.append(Component.text(" - " + map.getName() + " (" + map.size() + ")")).append(Component.newline());
            }
        }
        ctx.getSource().sendMessage(c);
        return 0;
    }
}
