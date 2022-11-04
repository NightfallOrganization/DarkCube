package eu.darkcube.system.pserver.plugin.command;

import org.bukkit.Bukkit;

import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class StopCommand extends PServerExecutor {

	public StopCommand() {
		super("stop", new String[] {
						"shutdown"
		}, b -> b.executes(source -> {
			source.getSource().sendFeedback(Message.SHUTTING_DOWN_SERVER.getMessage(source.getSource()), true);
			Bukkit.shutdown();
			return 0;
		}));
	}
}
