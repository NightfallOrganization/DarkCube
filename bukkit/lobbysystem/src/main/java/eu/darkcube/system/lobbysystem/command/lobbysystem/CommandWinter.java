/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem;

import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;

public class CommandWinter extends LobbyCommandExecutor {
	public CommandWinter() {
		super("winter", b -> b.then(
				Commands.argument("winter", BooleanArgument.booleanArgument()).executes(ctx -> {
					boolean winter = BooleanArgument.getBoolean(ctx, "winter");
					Lobby.getInstance().getDataManager().setWinter(winter);
					ctx.getSource().sendMessage(Component.text("Winter: " + winter));
					return 0;
				})));
	}
}
