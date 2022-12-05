package eu.darkcube.system.lobbysystem.command;

import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandBorder;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandBuild;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandJumpAndRun;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandMinigame;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandNPC;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandSetSpawn;
import eu.darkcube.system.lobbysystem.command.lobbysystem.CommandShowSkullCache;

public class CommandLobbysystem extends LobbyCommandExecutor {

	public CommandLobbysystem() {
		super("lobbysystem", b -> b.then(new CommandShowSkullCache().builder())
				.then(new CommandSetSpawn().builder()).then(new CommandMinigame().builder())
				.then(new CommandNPC().builder()).then(new CommandBuild().builder())
				.then(new CommandBorder().builder()).then(new CommandJumpAndRun().builder()));
	}

}
