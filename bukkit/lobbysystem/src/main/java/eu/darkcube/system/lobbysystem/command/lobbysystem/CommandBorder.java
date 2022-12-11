/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetPos1;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetPos2;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetRadius;
import eu.darkcube.system.lobbysystem.command.lobbysystem.border.CommandSetShape;

public class CommandBorder extends LobbyCommandExecutor {

	public CommandBorder() {
		super("border", b -> {
			b.then(new CommandSetPos1().builder()).then(new CommandSetPos2().builder())
					.then(new CommandSetRadius().builder()).then(new CommandSetShape().builder());
		});
	}

}
