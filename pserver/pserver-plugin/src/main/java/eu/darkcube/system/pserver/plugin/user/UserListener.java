/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import eu.darkcube.system.pserver.plugin.listener.BaseListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class UserListener implements BaseListener {

	@EventHandler
	public void handle(PlayerJoinEvent e) {
		UserManager.getInstance().playerJoin(e.getPlayer());
	}

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		UserManager.getInstance().playerQuit(e.getPlayer());
	}

	//	@EventHandler
	//	public void handle(BukkitCommandExecutorConfigureEvent e) {
	//		e.getExecutor().setMessagePrefix(
	//				new CustomComponentBuilder("[").color(ChatColor.DARK_GRAY).append("PServer")
	//						.color(ChatColor.GOLD).append("]").color(ChatColor.DARK_GRAY).append(" ")
	//						.color(ChatColor.GRAY).create());
	//	}

}
