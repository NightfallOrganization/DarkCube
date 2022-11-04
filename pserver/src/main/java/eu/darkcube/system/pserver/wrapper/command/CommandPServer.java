package eu.darkcube.system.pserver.wrapper.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.pserver.wrapper.PServerWrapper;

public class CommandPServer extends Command {

	public CommandPServer() {
		super(PServerWrapper.getInstance(), "pserver", new Command[0], "PServer");
		setAliases(new String[] {
				"ps", "s", "privateserver", "privatserver"
		});
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (PServerWrapper.getPServerCommand() != null) {
			return PServerWrapper.getPServerCommand().execute(sender, args);
		}
		sender.sendMessage("§cPServer not configured.");
		return true;
	}
}
