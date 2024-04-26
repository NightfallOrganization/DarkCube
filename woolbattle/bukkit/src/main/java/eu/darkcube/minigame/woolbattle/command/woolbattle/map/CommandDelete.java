/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandDelete extends WBCommand {
    public CommandDelete(WoolBattleBukkit woolbattle) {
        super("delete", b -> b.executes(ctx -> {
            Map map = MapArgument.getMap(ctx, "map");
            woolbattle.mapManager().deleteMap(map);
            ctx.getSource().sendMessage(Component.text("Du hast die Map " + map.getName() + " gelöscht!"));
            return 0;
        }));
    }
}
