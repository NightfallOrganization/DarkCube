package eu.darkcube.system.lobbysystem.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandBorder;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandBuild;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandGetCubes;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandMinigame;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandNPC;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandSetCubes;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandSetSpawn;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandShowSkullCache;

public class CommandLobbysystem extends Command {

	public CommandLobbysystem() {
		super(Lobby.getInstance(), "lobbysystem", new Command[] {
				new CommandSetSpawn(),
				new CommandBorder(),
				new CommandNPC(),
				new CommandMinigame(),
				new CommandBuild(),
				new CommandSetCubes(),
				new CommandGetCubes(),
				new CommandShowSkullCache()
		}, "Lobbysystem Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}
}