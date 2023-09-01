/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.DoubleArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.util.Border;

public class CommandSetRadius extends LobbyCommand {

	public CommandSetRadius() {
		super("setRadius", b -> b.then(
				Commands.argument("radius", DoubleArgumentType.doubleArg(10)).executes(ctx -> {
					double d = DoubleArgumentType.getDouble(ctx, "radius");
					Border border = Lobby.getInstance().getDataManager().getBorder();
					border = new Border(border.getShape(), d, border.getLoc1(), border.getLoc2());
					Lobby.getInstance().getDataManager().setBorder(border);
					ctx.getSource().sendMessage(
							Component.text("Radius neugesetzt!").color(NamedTextColor.GREEN));
					return 0;
				})));
	}

}
