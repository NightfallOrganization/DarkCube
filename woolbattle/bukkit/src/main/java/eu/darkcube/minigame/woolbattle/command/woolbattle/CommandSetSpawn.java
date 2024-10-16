/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.util.Locations;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.commandapi.argument.BooleanArgument;

public class CommandSetSpawn extends WBCommand {
    public CommandSetSpawn(WoolBattleBukkit woolbattle) {
        super("setSpawn", b -> b.then(Commands.argument("makeNice", BooleanArgument.booleanArgument()).executes(ctx -> {
            var p = ctx.getSource().asPlayer();
            var loc = p.getLocation();
            var makeNice = BooleanArgument.getBoolean(ctx, "makeNice");
            if (makeNice) {
                loc = Locations.getNiceLocation(loc);
                p.teleport(loc);
            }
            woolbattle.lobby().setSpawn(loc);
            p.sendMessage("§aDer LobbySpawn wurde umgesetzt!");
            return 0;
        })));
    }
}
