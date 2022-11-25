package eu.darkcube.system.miners.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.miners.Miners;

public class CommandTimer extends CommandExecutor {

	public CommandTimer() {
		super("miners", "timer", new String[0],
				b -> b.then(Commands.argument("time", IntegerArgumentType.integer(1)).executes(context -> {
					return setTime(IntegerArgumentType.getInteger(context, "time"));
				})));
	}

	public static int setTime(int secs) {
		if (!Miners.getLobbyPhase().getTimer().isRunning())
			Miners.getLobbyPhase().getTimer().start(secs * 1000);
		else
			Miners.getLobbyPhase().getTimer().setEndTime(System.currentTimeMillis() + secs * 1000);
		return 0;
	}

}
