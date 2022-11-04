package eu.darkcube.system.cloudban.module.command;

import java.util.UUID;

import de.dytanic.cloudnet.command.ICommandSender;
import eu.darkcube.system.cloudban.command.CommandSender;
import eu.darkcube.system.cloudban.util.ban.Ban;

public class CommandSenderCloud implements CommandSender {

	private ICommandSender handle;
	
	public CommandSenderCloud(ICommandSender handle) {
		this.handle = handle;
	}
	
	@Override
	public boolean hasPermission(String permission) {
		return handle.hasPermission(permission);
	}

	@Override
	public void sendMessage(String message) {
		handle.sendMessage(message);
	}

	@Override
	public String getName() {
		return "Console";
	}

	@Override
	public void sendMessage(String... message) {
		for(String msg : message)
			sendMessage(msg);
	}

	@Override
	public UUID getUniqueId() {
		return Ban.CONSOLE;
	}
}
