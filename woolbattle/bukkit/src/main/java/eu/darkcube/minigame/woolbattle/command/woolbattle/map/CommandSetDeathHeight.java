/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle.map;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandSetDeathHeight extends WBCommandExecutor {
    public CommandSetDeathHeight() {
        super("setDeathHeight", b -> b.then(
                Commands.argument("deathHeight", IntegerArgumentType.integer()).executes(ctx -> {
                    Map map = MapArgument.getMap(ctx, "map");
                    int deathHeight = IntegerArgumentType.getInteger(ctx, "deathHeight");
                    map.deathHeight(deathHeight);
                    ctx.getSource().sendMessage(Component.text("Todeshöhe gesetzt!"));
                    return 0;
                })));
    }
    //	public CommandSetDeathHeight() {
    //		super(WoolBattle.getInstance(), "setDeathHeight", new Command[0], "Setzt die Todeshöhe der Map",
    //				CommandArgument.DEATHHEIGHT);
    //	}
    //
    //	@Override
    //	public boolean execute(CommandSender sender, String[] args) {
    //		if (args.length == 1) {
    //			Map map = WoolBattle.getInstance().getMapManager().getMap(this.getSpaced());
    //			if (map == null) {
    //				sender.sendMessage("§cEs konnte keine Map mit dem Namen '" + this.getSpaced() + "'gefunden werden.");
    //				return true;
    //			}
    //			try {
    //				int height = Integer.parseInt(args[0]);
    //				map.setDeathHeight(height);
    //				sender.sendMessage("§aTodeshöhe umgesetzt!");
    //				return true;
    //			} catch (Exception ex) {
    //				sender.sendMessage("§cUngültige Zahl: " + args[0]);
    //				return true;
    //			}
    //		}
    //		return false;
    //	}

}
