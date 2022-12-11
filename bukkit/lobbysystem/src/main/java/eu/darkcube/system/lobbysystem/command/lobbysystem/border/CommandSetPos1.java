/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.border;

import com.mojang.brigadier.context.CommandContext;
import org.bukkit.Location;
import eu.darkcube.system.commandapi.v3.CommandSource;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.commandapi.v3.arguments.BooleanArgument;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.parser.Locations;
import eu.darkcube.system.lobbysystem.util.Border;
import eu.darkcube.system.lobbysystem.util.DataManager;
import net.md_5.bungee.api.ChatColor;

public class CommandSetPos1 extends LobbyCommandExecutor {

	public CommandSetPos1() {
		super("setPos1",
				b -> b.executes(ctx -> cmd(ctx, ctx.getSource().asPlayer().getLocation()))
						.then(Commands.argument("makenice", BooleanArgument.booleanArgument())
								.executes(ctx -> cmd(ctx,
										BooleanArgument.getBoolean(ctx, "makenice")
												? Locations.getNiceLocation(
														ctx.getSource().asPlayer().getLocation())
												: ctx.getSource().asPlayer().getLocation()))));
	}

	private static int cmd(CommandContext<CommandSource> ctx, Location loc) {
		DataManager data = Lobby.getInstance().getDataManager();
		Border border = data.getBorder();
		border = new Border(border.getShape(), border.getRadius(), loc, border.getLoc2());
		data.setBorder(border);
		ctx.getSource().sendFeedback(
				new CustomComponentBuilder("Position 1 gesetzt!").color(ChatColor.GREEN).create(),
				true);
		return 0;
	}

}
