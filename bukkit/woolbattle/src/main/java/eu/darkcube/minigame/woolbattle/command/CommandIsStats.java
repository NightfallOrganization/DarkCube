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

public class CommandIsStats extends Command {

	public CommandIsStats() {
		super(WoolBattle.getInstance(), "isStats", new Command[0], "IsStats");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		WoolBattle.getInstance().sendMessage(
				"§7Statistiken sind " + (StatsLink.enabled ? "§aaktiviert" : "§cdeaktiviert") + "!", sender);
		return true;
	}
}
