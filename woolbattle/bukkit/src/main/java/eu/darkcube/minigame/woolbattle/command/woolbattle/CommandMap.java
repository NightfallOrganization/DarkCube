/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.MapArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandDisable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandEnable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandInfo;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandSetIcon;
import eu.darkcube.system.commandapi.v3.Commands;

public class CommandMap extends WBCommandExecutor {
	public CommandMap() {
		super("map", b -> b.then(Commands.argument("map", MapArgument.mapArgument())
				.then(new CommandDisable().builder()).then(new CommandEnable().builder())
				.then(new CommandInfo().builder()).then(new CommandSetIcon().builder())));
	}
	//	public CommandMap() {
	//		super(WoolBattle.getInstance(), "map", new SubCommand[] {
	//				new CommandSetIcon(), new CommandEnable(), new CommandDisable(), new CommandInfo(),
	//				new CommandSetDeathHeight()
	//		}, "Map HauptCommand", CommandArgument.MAP);
	//	}
	//
	//	@Override
	//	public boolean execute(CommandSender paramCommandSender, String[] paramArrayOfString) {
	//		return false;
	//	}
	//
	//	@Override
	//	public List<String> onTabComplete(String[] args) {
	//		if (args.length == 1) {
	//			return Arrays.toSortedStringList(WoolBattle.getInstance().getMapManager().getMaps(), args[0]);
	//		}
	//		return super.onTabComplete(args);
	//	}

}
