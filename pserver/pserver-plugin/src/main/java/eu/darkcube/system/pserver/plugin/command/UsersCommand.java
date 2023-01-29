/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command;

import org.bukkit.entity.Player;

import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;
import eu.darkcube.system.pserver.plugin.inventory.UserManagmentInventory;
import eu.darkcube.system.pserver.plugin.user.UserManager;

public class UsersCommand extends PServerExecutor {

	public UsersCommand() {
		super("users", new String[0], b -> b.executes(context -> {
			Player p = context.getSource().asPlayer();
			new UserManagmentInventory(
							UserManager.getInstance().getUser(p)).open();
			return 0;
		}));
	}
}
