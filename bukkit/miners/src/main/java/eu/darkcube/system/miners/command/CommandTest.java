package eu.darkcube.system.miners.command;

import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.miners.Miners;

public class CommandTest extends CommandExecutor {

	public CommandTest() {
		super("miners", "test", "miners.test", new String[0], b -> b.executes(context -> {
			return test(context.getSource().asPlayer(), 10);
		}));
	}

	public static int test(Player target, int secs) {
		if (!Miners.getLobbyPhase().getTimer().isRunning())
			Miners.getLobbyPhase().getTimer().start(secs * 1000);
		else
			Miners.getLobbyPhase().getTimer().cancel(false);
		return 0;
	}

}
