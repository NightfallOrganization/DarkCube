package eu.darkcube.system.commandapi.v2;

import java.util.Collection;
import java.util.Properties;

public interface ITabCompleter {
	
	/**
	 * This method allows on a command implementation to complete the tab requests from the sender.
	 * 
	 * @param commandLine	currently written command line
	 * @param args			command line split into arguments
	 * @param properties	parsed properties from the command line
	 * @return all available results.
	 */
	Collection<String> tabComplete(String commandLine, String[] args, Properties properties);

}
