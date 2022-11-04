package eu.darkcube.system.pserver.plugin.command;

import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.pserver.plugin.Message;
import eu.darkcube.system.pserver.plugin.command.impl.PServerExecutor;

public class CommandsCommand extends PServerExecutor {

	public CommandsCommand() {
		super("commands", new String[] {
						"befehle", "help"
		}, b -> b.executes(context -> {
			CommandSource source = context.getSource();
			source.sendFeedback(Message.COMMANDS_PREFIX.getMessage(source, PServerExecutor.COMMAND_NAMES.size()), true);
			for (String name : PServerExecutor.COMMAND_NAMES) {
				source.sendFeedback(Message.COMMANDS_COMMANDINFO.getMessage(source, name), true);
			}
			return 0;
		}));
	}

}
