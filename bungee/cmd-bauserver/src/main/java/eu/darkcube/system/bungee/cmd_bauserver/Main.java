/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bungee.cmd_bauserver;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin {

	@Override
	public void onEnable() {
		Command cmd = new Command("bauserver", "darkcube.bauserver") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				if (hasPermission(sender) && sender instanceof ProxiedPlayer) {
					ProxiedPlayer p = (ProxiedPlayer) sender;
					ServerInfo info = getProxy().getServerInfo("bauserver-1");
					if (info == null) {
						p.sendMessage(new TextComponent("§cKonnte bauserver-1 nicht finden!"));
						return;
					}
					p.connect(info);
				}
			}
		};
		getProxy().getPluginManager().registerCommand(this, cmd);
		cmd = new Command("pengu", "darkcube.pengu") {

			@Override
			public void execute(CommandSender sender, String[] args) {
				if (hasPermission(sender) && sender instanceof ProxiedPlayer) {
					ProxiedPlayer p = (ProxiedPlayer) sender;
					ServerInfo info = getProxy().getServerInfo("pengu-1");
					if (info == null) {
						p.sendMessage(new TextComponent("§cKonnte pengu-1 nicht finden!"));
						return;
					}
					p.connect(info);
				}
			}
		};
		getProxy().getPluginManager().registerCommand(this, cmd);
	}

}
