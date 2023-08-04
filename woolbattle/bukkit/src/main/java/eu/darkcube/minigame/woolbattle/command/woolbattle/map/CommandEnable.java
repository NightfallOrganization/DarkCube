/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;

public class CommandEnable extends WBCommandExecutor {
    public CommandEnable() {
        super("enable", b -> b.executes(ctx -> {
            Map map = MapArgument.getMap(ctx, "map");
            if (map.isEnabled()) {
                ctx.getSource().sendMessage(Component.text("Diese Map ist bereits aktiviert!")
                        .color(NamedTextColor.RED));
            } else {
                map.enable();
                WoolBattle.instance().lobby().recalculateMap();
                ctx.getSource().sendMessage(
                        Component.text("Du hast die Map '" + map.getName() + "' aktiviert!")
                                .color(NamedTextColor.GREEN));
            }
            return 0;
        }));
    }
}