package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetDailyReward;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetWoolBattle;

public class CommandNPC extends LobbyCommandExecutor {

	public CommandNPC() {
		super("npc", b -> {
			b.then(new CommandSetWoolBattle().builder())
					.then(new CommandSetDailyReward().builder());
		});
	}

}
