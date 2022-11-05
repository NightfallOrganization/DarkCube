package eu.darkcube.system.lobbysystem.command.lobbysystem;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetPos1;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetPos2;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetRadius;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetShape;

public class CommandBorder extends Command {

	public CommandBorder() {
		super(Lobby.getInstance(), "border", new Command[] {
				new CommandSetPos1(),
				new CommandSetPos2(),
				new CommandSetShape(),
				new CommandSetRadius()
		}, "Border Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}
}
