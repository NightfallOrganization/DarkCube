package eu.darkcube.system.miners.command;

import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.v3.CommandExecutor;

public class CommandTest extends CommandExecutor {

	public CommandTest() {
		super("miners", "test", new String[0], b -> b.executes(context -> {
			return test(context.getSource().asPlayer());
		}));
	}

	public static int test(Player p) {
		p.setPlayerListName("abrbs");
		return 0;
	}

}
