/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.cloudban.bungee.command;

import java.util.Arrays;

import eu.darkcube.system.cloudban.bungee.Main;
import eu.darkcube.system.cloudban.command.wrapper.CommandWrapper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.plugin.TabExecutor;

public class BungeeCommandWrapper extends Command implements TabExecutor {

	private eu.darkcube.system.cloudban.command.Command handle;

	public static void create() {
		CommandWrapper.load();
		for(eu.darkcube.system.cloudban.command.Command cmd : eu.darkcube.system.cloudban.command.Command.COMMANDS) {
			new BungeeCommandWrapper(cmd);
		}
	}
	
	private BungeeCommandWrapper(eu.darkcube.system.cloudban.command.Command handle) {
		super(handle.getNames()[0], handle.getPermission(),
				Arrays.copyOfRange(handle.getNames(), 1, handle.getNames().length));
		this.handle = handle;
		registerCommand(Main.getInstance());
	}

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		return handle.onTabComplete(new CommandSenderBungee(sender), getName(), args, getCommandLine(args));
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		handle.execute(new CommandSenderBungee(sender), getName(), args, getCommandLine(args));
	}
	
	private String getCommandLine(String[] args) {
		StringBuilder b = new StringBuilder();
		b.append(getName());
		for(String s : args)
			b.append(" ").append(s);
		return b.toString();
	}

	private void registerCommand(Plugin plugin) {
		PluginManager pm = ProxyServer.getInstance().getPluginManager();
		pm.registerCommand(plugin, this);
	}
}
