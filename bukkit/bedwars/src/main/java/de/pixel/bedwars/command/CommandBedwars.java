package de.pixel.bedwars.command;

import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.command.bedwars.CommandCreateMap;
import de.pixel.bedwars.command.bedwars.CommandCreateTeam;
import de.pixel.bedwars.command.bedwars.CommandDeleteMap;
import de.pixel.bedwars.command.bedwars.CommandDeleteTeam;
import de.pixel.bedwars.command.bedwars.CommandMap;
import de.pixel.bedwars.command.bedwars.CommandSetLobbySpawn;
import de.pixel.bedwars.command.bedwars.CommandSpawner;
import de.pixel.bedwars.command.bedwars.CommandTeam;
import eu.darkcube.system.commandapi.Command;

public class CommandBedwars extends Command {

	public CommandBedwars() {
		super(Main.getInstance(), "bedwars",
				new Command[] { new CommandCreateTeam(), new CommandDeleteTeam(), new CommandTeam(),
						new CommandCreateMap(), new CommandDeleteMap(), new CommandMap(), new CommandSetLobbySpawn(),
						new CommandSpawner() },
				"Bedwars Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender arg0, String[] arg1) {
		return false;
	}
}
