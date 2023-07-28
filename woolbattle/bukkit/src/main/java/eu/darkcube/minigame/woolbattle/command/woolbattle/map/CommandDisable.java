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

public class CommandDisable extends WBCommandExecutor {
    public CommandDisable() {
        super("disable", b -> b.executes(ctx -> {
            Map map = MapArgument.getMap(ctx, "map");
            if (!map.isEnabled()) {
                ctx.getSource().sendMessage(Component.text("Diese Map ist bereits deaktiviert!")
                        .color(NamedTextColor.RED));
            } else {
                map.disable();
                WoolBattle.instance().lobby().recalculateMap();
                ctx.getSource().sendMessage(
                        Component.text("Du hast die Map '" + map.getName() + "' deaktiviert!")
                                .color(NamedTextColor.GREEN));
            }
            return 0;
        }));
    }
    //	public CommandDisable() {
    //		super(WoolBattle.getInstance(), "disable", new Command[0], "Deaktiviert die Map");
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		if (args.length == 0) {
    //			Map map = WoolBattle.getInstance().getMapManager().getMap(getSpaced());
    //			if (map == null) {
    //				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + getSpaced() + "'gefunden werden.");
    //				return true;
    //			}
    //			if (!map.isEnabled()) {
    //				sender.sendMessage("§cDiese Map ist bereits deaktiviert!");
    //				return true;
    //			}
    //			map.disable();
    //			WoolBattle.getInstance().getLobby().recalculateMap();
    //			sender.sendMessage("§aDu hast die Map '" + map.getName() + "' deaktiviert!");
    //			return true;
    //		}
    //		return false;
    //	}

}
