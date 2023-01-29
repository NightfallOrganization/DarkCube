/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi.command;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.util.Language;

public class CommandLanguage extends CommandExecutor {

	public CommandLanguage() {
		super("user", "language", new String[0],
				b -> b.then(
						Commands.argument("language", EnumArgument.enumArgument(Language.values()))
								.executes(ctx -> {

									return 0;
								})));
	}

}
