/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.CommandSetSpawn;
import eu.darkcube.minigame.woolbattle.command.woolbattle.team.*;
import eu.darkcube.system.commandapi.v3.Commands;

public class CommandTeam extends WBCommandExecutor {
	public CommandTeam() {
		super("team", b -> b.then(Commands.argument("team", TeamArgument.teamArgument())
				.then(new CommandDisable().builder()).then(new CommandEnable().builder())
				.then(new CommandInfo().builder()).then(new CommandSetNameColor().builder())
				.then(new CommandSetSpawn().builder()).then(new CommandSetWoolColor().builder())));
	}

	//	public CommandTeam() {
	//		super(WoolBattle.getInstance(), "team",
	//				new SubCommand[] { new CommandSetSpawn(), new CommandDisable(), new CommandEnable(),
	//						new CommandSetNameColor(), new CommandSetWoolColor(), new CommandInfo() },
	//				"Team Hauptcommand", CommandArgument.TEAM);
	//	}
	//
	//	@Override
	//	public List<String> onTabComplete(String[] args) {
	//		if (args.length == 1) {
	//			return Arrays.toSortedStringList(TeamType.values(), args[0]);
	//		}
	//		return super.onTabComplete(args);
	//	}
	//
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		return false;
	//	}
}
