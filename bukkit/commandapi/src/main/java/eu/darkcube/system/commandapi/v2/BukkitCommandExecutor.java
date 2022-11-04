package eu.darkcube.system.commandapi.v2;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class BukkitCommandExecutor implements ICommandExecutor {

	private CommandSender sender;
	private String messageFormat = "%1$s";

	public BukkitCommandExecutor(CommandSender sender) {
		this.sender = sender;
		Bukkit.getPluginManager().callEvent(new BukkitCommandExecutorConfigureEvent(this));
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public void sendMessage(String message) {
		sender.sendMessage(String.format(this.messageFormat, message));
	}

	public CommandSender getSender() {
		return sender;
	}

	@Override
	public void setMessageFormat(String format) {
		this.messageFormat = format;
	}

	@Override
	public String getMessageFormat() {
		return this.messageFormat;
	}
}
