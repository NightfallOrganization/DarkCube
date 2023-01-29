/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;

public class UUIDManager {

	public static IPlayerManager getManager() {
		return CloudNetDriver.getInstance().getServicesRegistry()
				.getFirstService(IPlayerManager.class);
	}

	public static CommandSender getSender(String name) {
		if (name.equals("console"))
			return Bukkit.getConsoleSender();
		return Bukkit.getPlayer(name);
	}
}
