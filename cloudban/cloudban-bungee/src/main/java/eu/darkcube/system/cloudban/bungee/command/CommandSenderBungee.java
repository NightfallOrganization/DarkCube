package eu.darkcube.system.cloudban.bungee.command;

import java.util.UUID;

import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.util.ban.Ban;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class CommandSenderBungee implements CommandSender {

	private net.md_5.bungee.api.CommandSender handle;
	
	public CommandSenderBungee(net.md_5.bungee.api.CommandSender handle) {
		this.handle = handle;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return handle.hasPermission(permission);
	}

	@Override
	public void sendMessage(String message) {
		handle.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
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
		if(!(handle instanceof ProxiedPlayer)) {
			return Ban.CONSOLE;
		}
		return ((ProxiedPlayer)handle).getUniqueId();
	}
}
