package eu.darkcube.system.lobbysystem.command.lobbysystem.minigame;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandAddTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandListTasks;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandRemoveTask;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.woolbattle.CommandSetSpawn;

public class CommandWoolBattle extends LobbyCommandExecutor {

	public CommandWoolBattle() {
		super("woolbattle", b -> {
			b.then(new CommandAddTask().builder()).then(new CommandListTasks().builder())
					.then(new CommandRemoveTask().builder()).then(new CommandSetSpawn().builder());
		});
	}

}
