package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun.CommandCreateRegion;
import eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun.CommandDeleteRegion;
import eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun.CommandListRegions;
import eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun.CommandSetPlate;
import eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun.CommandSetSpawn;

public class CommandJumpAndRun extends LobbyCommandExecutor {

	public CommandJumpAndRun() {
		super("jumpAndRun", b -> b.then(new CommandListRegions().builder())
				.then(new CommandCreateRegion().builder()).then(new CommandDeleteRegion().builder())
				.then(new CommandSetSpawn().builder()).then(new CommandSetPlate().builder()));
	}

}
