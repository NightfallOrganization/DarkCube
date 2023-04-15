/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.StringArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandCreateMap extends WBCommandExecutor {
	public CommandCreateMap() {
		super("createMap",
				b -> b.then(Commands.argument("map", StringArgument.string()).executes(ctx -> {
					String mname = StringArgument.getString(ctx, "map");
					Map map = WoolBattle.instance().getMapManager().getMap(mname);
					if (map != null) {
						ctx.getSource().sendMessage(Component.text(
								"Es gibt bereits eine Map mit dem Namen '" + mname + "'."));
					} else {
						map = WoolBattle.instance().getMapManager().createMap(mname);
						ctx.getSource().sendMessage(
								Component.text("Du hast die Map " + map.getName() + " erstellt!"));
					}
					return 0;
				})));
	}
	//	public CommandCreateMap() {
	//		super(WoolBattle.getInstance(), "createMap", new Command[0], "Erstellt eine Map", CommandArgument.MAP);
	//	}
	//
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (args.length == 1) {
	//			Map map = WoolBattle.getInstance().getMapManager().getMap(args[0]);
	//			if (map != null) {
	//				sender.sendMessage("§cEs gibt bereits eine Map mit dem Namen '" + args[0] + "'.");
	//				return true;
	//			}
	//			map = WoolBattle.getInstance().getMapManager().createMap(args[0]);
	//			sender.sendMessage("§aDu hast die Map '" + map.getName() + "' erstellt!");
	//			return true;
	//		}
	//		return false;
	//	}

}
