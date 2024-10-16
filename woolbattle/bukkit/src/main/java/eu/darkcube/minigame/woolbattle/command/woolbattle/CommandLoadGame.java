/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;

public class CommandLoadGame extends WBCommand {
    public CommandLoadGame(WoolBattleBukkit woolbattle) {
        super("loadGame", b -> b.then(Commands.argument("mapSize", MapSizeArgument.mapSize(woolbattle))
                .requires(source -> woolbattle.lobby().enabled())
                .executes(ctx -> {
                    MapSize mapSize = MapSizeArgument.mapSize(ctx, "mapSize");
                    woolbattle.lobby().loadGame(mapSize);
                    ctx.getSource().sendMessage(Component.text("Spiel geladen").color(NamedTextColor.GREEN));
                    return 0;
                }))
        );
    }
}
