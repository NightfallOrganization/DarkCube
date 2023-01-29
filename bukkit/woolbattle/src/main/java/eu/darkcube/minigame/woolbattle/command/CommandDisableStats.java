/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.commandapi.Command;

public class CommandDisableStats extends Command {

	public CommandDisableStats() {
		super(WoolBattle.getInstance(), "disablestats", new Command[0], "Deaktiviert die Stats");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!StatsLink.isStats()) {
			WoolBattle.getInstance().sendMessage("§cStats sind bereits deaktiviert!", sender);
		} else {
			StatsLink.enabled = false;
			WoolBattle.getInstance()
					.sendMessage("§aStats wurden deaktiviert! Alle weiteren Kills/Deaths/etc zählen nicht", sender);
		}
		return true;
	}
}
