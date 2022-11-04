package eu.darkcube.system.cloudban.command.wrapper;

import eu.darkcube.system.cloudban.command.commands.CommandBan;
import eu.darkcube.system.cloudban.command.commands.CommandBanSystem;
import eu.darkcube.system.cloudban.command.commands.CommandUnban;

public class CommandWrapper {

	public static void load() {
		new CommandBan();
		new CommandUnban();
		new CommandBanSystem();
	}
}
