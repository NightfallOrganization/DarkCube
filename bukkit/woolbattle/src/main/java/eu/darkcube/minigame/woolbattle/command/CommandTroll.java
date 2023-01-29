/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerk;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerkCooldown;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerkCost;
import eu.darkcube.minigame.woolbattle.command.troll.CommandToggle;
import eu.darkcube.minigame.woolbattle.command.troll.CommandVanish;
import eu.darkcube.system.commandapi.Command;

public class CommandTroll extends Command {

	public CommandTroll() {
		super(WoolBattle.getInstance(), "troll", new Command[] {
				new CommandSetPerkCooldown(), new CommandToggle(), new CommandSetPerkCost(), new CommandSetPerk(), new CommandVanish()
		}, "Troll Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender arg0, String[] arg1) {
		return false;
	}

}
