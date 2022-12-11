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
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.commandapi.Command;

public class CommandFix extends Command {

	public CommandFix() {
		super(WoolBattle.getInstance(), "fix", new Command[0], "Fix");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player && WoolBattle.getInstance().getIngame().isEnabled()) {
			new Scheduler() {
				@Override
				public void run() {
					Player p = (Player) sender;
					User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
					WoolBattle.getInstance().getIngame().setPlayerItems(user);
				}
			}.runTaskLater(1);
		}
		return true;
	}
}
