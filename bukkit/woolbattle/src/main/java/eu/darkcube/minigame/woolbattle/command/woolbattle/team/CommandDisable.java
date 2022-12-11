/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command.woolbattle.team;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandDisable extends SubCommand {

	public CommandDisable() {
		super(WoolBattle.getInstance(), "disable", new Command[0], "Deaktiviert das Team");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			TeamType team = TeamType.byDisplayNameKey(getSpaced());
			if (team == null || team.isDeleted()) {
				sender.sendMessage("§cEs konnte kein Team mit dem Namen '" + getSpaced() + "' gefunden werden.");
				return true;
			}
			if (!team.isEnabled()) {
				sender.sendMessage("§cDieses Team ist bereits deaktiviert!");
				return true;
			}
			team.setEnabled(false);
			sender.sendMessage("§7Du hast das Team '" + team.getDisplayNameKey() + "' deaktiviert!");
			return true;
		}
		return false;
	}
}
