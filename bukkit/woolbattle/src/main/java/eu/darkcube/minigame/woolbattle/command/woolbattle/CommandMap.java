/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandDisable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandEnable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandInfo;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandSetDeathHeight;
import eu.darkcube.minigame.woolbattle.command.woolbattle.map.CommandSetIcon;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.SpacedCommand;

public class CommandMap extends SpacedCommand {

	public CommandMap() {
		super(WoolBattle.getInstance(), "map", new SubCommand[] {
				new CommandSetIcon(), new CommandEnable(), new CommandDisable(), new CommandInfo(),
				new CommandSetDeathHeight()
		}, "Map HauptCommand", CommandArgument.MAP);
	}

	@Override
	public boolean execute(CommandSender paramCommandSender, String[] paramArrayOfString) {
		return false;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(WoolBattle.getInstance().getMapManager().getMaps(), args[0]);
		}
		return super.onTabComplete(args);
	}

}
