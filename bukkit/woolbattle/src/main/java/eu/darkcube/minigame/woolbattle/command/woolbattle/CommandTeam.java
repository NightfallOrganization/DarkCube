/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.util.List;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetNameColor;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandEnable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandInfo;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandDisable;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetSpawn;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetWoolColor;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.system.commandapi.SpacedCommand;

public class CommandTeam extends SpacedCommand {

	public CommandTeam() {
		super(WoolBattle.getInstance(), "team",
				new SubCommand[] { new CommandSetSpawn(), new CommandDisable(), new CommandEnable(),
						new CommandSetNameColor(), new CommandSetWoolColor(), new CommandInfo() },
				"Team Hauptcommand", CommandArgument.TEAM);
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Arrays.toSortedStringList(TeamType.values(), args[0]);
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}
}
