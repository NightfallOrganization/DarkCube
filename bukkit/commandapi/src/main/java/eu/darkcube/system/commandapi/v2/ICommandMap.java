package eu.darkcube.system.commandapi.v2;

import java.util.Collection;
import java.util.List;
import java.util.Properties;

public interface ICommandMap {

	void registerCommand(Command command);

	void unregisterCommand(Command command);

	void unregisterCommand(Class<? extends Command> command);

	void unregisterCommands(ClassLoader classLoader);

	void unregisterCommands();

	List<String> tabCompleteCommand(String commandLine);

	List<String> tabCompleteCommand(String[] args, Properties properties);

	Command getCommand(String commandName);

	Command getCommandFromLine(String commandLine);
	
	Collection<Command> getCommands();

	boolean dispatchCommand(ICommandExecutor commandSender, String commandLine);

	default void registerCommand(Command... commands) {
		if (commands != null) {
			for (Command command : commands) {
				if (command != null) {
					registerCommand(command);
				}
			}
		}
	}

	default void unregisterCommand(Command... commands) {
		if (commands != null) {
			for (Command command : commands) {
				if (command != null) {
					unregisterCommand(command);
				}
			}
		}
	}

	default void unregisterCommand(@SuppressWarnings("unchecked") Class<? extends Command>... commands) {
		if (commands != null) {
			for (Class<? extends Command> c : commands) {
				if (c != null) {
					unregisterCommand(c);
				}
			}
		}
	}

}
