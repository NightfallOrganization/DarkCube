/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandDeleteMap extends WBCommandExecutor {
    public CommandDeleteMap(WoolBattle woolbattle) {
        super("deleteMap", b -> b
                .then(Commands.argument("map", MapArgument.mapArgument(woolbattle)).executes(ctx -> {
                    Map map = MapArgument.getMap(ctx, "map");
                    woolbattle.mapManager().deleteMap(map);
                    ctx.getSource().sendMessage(Component.text("Du hast die Map " + map.getName() + " gelöscht!"));
                    return 0;
                })));
    }

    //	public CommandDeleteMap() {
    //		super(WoolBattle.getInstance(), "deleteMap", new Command[0], "Löscht eine Map", CommandArgument.MAP);
    //	}
    //
    //	@Override
    //	public List<String> onTabComplete(String[] args) {
    //		if (args.length == 1) {
    //			return Arrays.toSortedStringList(WoolBattle.getInstance().getMapManager().getMaps(), args[0]);
    //		}
    //		return super.onTabComplete(args);
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		if (args.length == 1) {
    //			Map map = WoolBattle.getInstance().getMapManager().getMap(args[0]);
    //			if (map == null) {
    //				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + args[0] + "'gefunden werden.");
    //				return true;
    //			}
    //			map.delete();
    //			sender.sendMessage("§aDu hast die Map '" + map.getName() + "' gelöscht!");
    //			return true;
    //		}
    //		return false;
    //	}
}
