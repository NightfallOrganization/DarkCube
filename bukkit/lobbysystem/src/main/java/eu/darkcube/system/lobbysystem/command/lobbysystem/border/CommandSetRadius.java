/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.util.Border;
import net.md_5.bungee.api.ChatColor;

public class CommandSetRadius extends LobbyCommandExecutor {

	public CommandSetRadius() {
		super("setRadius", b -> b.then(
				Commands.argument("radius", DoubleArgumentType.doubleArg(10)).executes(ctx -> {
					double d = DoubleArgumentType.getDouble(ctx, "radius");
					Border border = Lobby.getInstance().getDataManager().getBorder();
					border = new Border(border.getShape(), d, border.getLoc1(), border.getLoc2());
					Lobby.getInstance().getDataManager().setBorder(border);
					ctx.getSource().sendFeedback(new CustomComponentBuilder("Radius neugesetzt!")
							.color(ChatColor.GREEN).create(), true);
					return 0;
				})));
	}

}
