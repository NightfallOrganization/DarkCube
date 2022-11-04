package eu.darkcube.system.commandapi.v2.sub;

import java.util.Map;
import java.util.Properties;

import eu.darkcube.system.commandapi.v2.ICommandExecutor;

public interface SubCommandExecutor {

	void execute(SubCommand subCommand, ICommandExecutor executor, String command,
			SubCommandArgumentWrapper args,
			String commandLine, Properties properties, Map<String, Object> internalProperties);

}