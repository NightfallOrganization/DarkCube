/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
