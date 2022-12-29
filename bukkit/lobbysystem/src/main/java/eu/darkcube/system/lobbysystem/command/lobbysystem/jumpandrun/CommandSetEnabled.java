/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;

public class CommandSetEnabled extends LobbyCommandExecutor {
	public CommandSetEnabled() {
		super("setEnabled", b -> b.then(
				Commands.argument("enabled", BooleanArgument.booleanArgument()).executes(ctx -> {
					boolean s = BooleanArgument.getBoolean(ctx, "enabled");
					Lobby.getInstance().getDataManager().setJumpAndRunEnabled(s);
					ctx.getSource().sendMessage(Component.text("JumpAndRun Enabled: " + s));
					return 0;
				})));
	}
}
