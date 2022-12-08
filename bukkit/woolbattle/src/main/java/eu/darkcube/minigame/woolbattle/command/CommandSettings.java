/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.user.UserSettings;
import eu.darkcube.system.commandapi.Command;

public class CommandSettings extends Command {

	public CommandSettings() {
		super(WoolBattle.getInstance(), "settings", new Command[0], "Einstellungen");
		setAliases("einstellungen");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		UserSettings.openSettings(user);
		return true;
	}
}
