package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.minigame.CommandWoolBattle;

public class CommandMinigame extends LobbyCommandExecutor {

	public CommandMinigame() {
		super("minigame", b -> {
			b.then(new CommandWoolBattle().builder());
		});
	}

}
