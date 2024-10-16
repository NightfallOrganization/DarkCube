/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.command.argument.MapSizeArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.*;
import eu.darkcube.system.bukkit.commandapi.Commands;

public class CommandMap extends WBCommand {
    public CommandMap(WoolBattleBukkit woolbattle) {
        super("map", b -> b.then(Commands
                .argument("mapSize", MapSizeArgument.mapSize(woolbattle))
                .then(Commands
                        .argument("map", MapArgument.mapArgument(woolbattle, MapArgument.ToStringFunction.function(ctx -> MapSizeArgument.mapSize(ctx, "mapSize"))))
                        .then(new CommandDisable(woolbattle).builder())
                        .then(new CommandEnable(woolbattle).builder())
                        .then(new CommandInfo().builder())
                        .then(new CommandSetDeathHeight().builder())
                        .then(new CommandSetIcon().builder())
                        .then(new CommandLoad(woolbattle).builder())
                        .then(new CommandDelete(woolbattle).builder()))));
    }

}
