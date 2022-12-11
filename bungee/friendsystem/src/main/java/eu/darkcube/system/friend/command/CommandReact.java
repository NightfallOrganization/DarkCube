/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.friend.command;

import eu.darkcube.system.friend.Arrays;
import eu.darkcube.system.friend.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandReact extends Command {

	public CommandReact() {
		super("react", "bungeecord.command.message", new String[] { "r" });
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			ProxiedPlayer t = CommandMessage.reactions.get(p);
			if (t == null) {
				Main.sendMessage(p, "§cDu hast mit keinem Spieler ein Gespräch angefangen!");
				return;
			}
			CommandMessage.instance.execute(p, Arrays.addBefore(args, t.getName()));
		}
	}
}
