/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.pserver;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServer;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.pserver.bukkit.PServerWrapper;
import eu.darkcube.system.pserver.bukkit.command.PServerCommand;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.userapi.UserAPI;

public class PServerSupport {

	private static Boolean supported = null;

	public static boolean isSupported() {
		return supported == null ? false : supported.booleanValue();
	}

	static {
		try {
			// eu.darkcube.system.pserver.server.PServer.class.getClass();
			Class.forName(PServer.class.getName());
			supported = true;
			Register.register();
		} catch (Throwable ex) {
			supported = false;
		}
	}

	public static void init() {

	}

	private static class Register {
		private static void register() {
			PServerWrapper.setPServerCommand(new PServerCommand() {
				@Override
				public boolean execute(CommandSender sender, String[] args) {
					if (sender instanceof Player) {
						LobbyUser user = UserWrapper
								.fromUser(UserAPI.getInstance().getUser((Player) sender));
						user.setOpenInventory(new InventoryPServer(user));
					}
					return true;
				}
			});
		}
	}
}
