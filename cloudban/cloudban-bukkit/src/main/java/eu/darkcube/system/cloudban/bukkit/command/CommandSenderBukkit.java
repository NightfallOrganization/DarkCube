/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.bukkit.command;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.cloudban.util.ban.Ban;

public class CommandSenderBukkit implements eu.darkcube.system.cloudban.command.CommandSender {

	private CommandSender handle;

	public CommandSenderBukkit(CommandSender handle) {
		this.handle = handle;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return handle.hasPermission(permission);
	}

	@Override
	public void sendMessage(String message) {
		handle.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	@Override
	public String getName() {
		return handle.getName();
	}

	@Override
	public void sendMessage(String... message) {
		for(String msg : message)
			sendMessage(msg);
	}

	@Override
	public UUID getUniqueId() {
		if(!(handle instanceof Player)) {
			return Ban.CONSOLE;
		}
		return ((Player)handle).getUniqueId();
	}
}
