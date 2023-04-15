/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.command.woolbattle;

import eu.darkcube.minigame.woolbattle.command.WBCommandExecutor;
import eu.darkcube.minigame.woolbattle.command.argument.TeamArgument;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;

public class CommandDeleteTeam extends WBCommandExecutor {
	public CommandDeleteTeam() {
		super("deleteTeam",
				b -> b.then(Commands.argument("team", TeamArgument.teamArgument()).executes(ctx -> {
					TeamType type = TeamArgument.getTeam(ctx, "team");
					type.delete();
					ctx.getSource().sendMessage(
							Component.text("Team " + type.getDisplayNameKey() + " gelöscht!"));
					return 0;
				})));
	}
	//	public CommandDeleteTeam() {
	//		super(WoolBattle.getInstance(), "deleteTeam", new Command[] {}, "Löscht ein Team", CommandArgument.TEAM);
	//	}
	//
	//	@Override
	//	public List<String> onTabComplete(String[] args) {
	//		if(args.length == 1) {
	//			return Arrays.toSortedStringList(TeamType.values(), args[0]);
	//		}
	//		return super.onTabComplete(args);
	//	}
	//
	//	@Override
	//	public boolean execute(CommandSender sender, String[] args) {
	//		if (args.length == 1) {
	//			TeamType team = TeamType.byDisplayNameKey(args[0]);
	//			if (team == null || team.isDeleted()) {
	//				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + args[0] + "' gefunden werden.");
	//				return true;
	//			}
	//			team.delete();
	//			sender.sendMessage("§aDu hast das Team '" + team.getDisplayNameKey() + "' gelöscht!");
	//			return true;
	//		}
	//		return false;
	//	}
}
