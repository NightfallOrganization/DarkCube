/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import java.util.Locale;

import eu.darkcube.minigame.woolbattle.command.WBCommand;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class CommandInfo extends WBCommand {
    public CommandInfo() {
        super("info", b -> b.executes(ctx -> {
            Map map = MapArgument.getMap(ctx, "map");
            String sb = "§aMap: " + map.getName() + "\nIcon: " + map.getIcon().getType().name().toLowerCase(Locale.ROOT) + "DeathHeight: " + (map.ingameData() == null ? "Unknown" : map.deathHeight()) + "\nAktiviert: " + map.isEnabled();
            ctx.getSource().sendMessage(LegacyComponentSerializer.legacySection().deserialize(sb));
            return 0;
        }));
    }
}
