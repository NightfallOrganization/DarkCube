/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.arguments.EnumArgument;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.util.Border;
import eu.darkcube.system.lobbysystem.util.Border.Shape;

public class CommandSetShape extends LobbyCommandExecutor {

	public CommandSetShape() {
		super("setShape", b -> b.then(
				Commands.argument("shape", EnumArgument.enumArgument(Shape.values()))
						.executes(ctx -> {
							Shape s = EnumArgument.getEnumArgument(ctx, "shape", Shape.class);
							Border border = Lobby.getInstance().getDataManager().getBorder();
							border = new Border(s, border.getRadius(), border.getLoc1(),
									border.getLoc2());
							Lobby.getInstance().getDataManager().setBorder(border);
							ctx.getSource().sendMessage(
									Component.text("Form neugesetzt!").color(NamedTextColor.GREEN));
							return 0;
						})));
	}

}
