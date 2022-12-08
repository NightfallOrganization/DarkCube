/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.jumpandrun.JaRRegion;
import net.md_5.bungee.api.ChatColor;

public class CommandDeleteRegion extends LobbyCommandExecutor {

	public CommandDeleteRegion() {
		super("deleteRegion",
				b -> b.then(Commands
						.argument("index",
								IntegerArgumentType.integer(0,
										Lobby.getInstance().getJaRManager().getRegions().size()))
						.executes(ctx -> {
							JaRRegion re = Lobby.getInstance().getJaRManager().getRegions()
									.remove(IntegerArgumentType.getInteger(ctx, "index"));
							Lobby.getInstance().getJaRManager().saveRegions();
							ctx.getSource().sendFeedback(
									new CustomComponentBuilder("Region ").color(ChatColor.GREEN)
											.append(re.toString()).color(ChatColor.DARK_PURPLE)
											.append("(").color(ChatColor.GRAY)
											.append(Integer.toString(
													IntegerArgumentType.getInteger(ctx, "index")))
											.color(ChatColor.GOLD).append(")").color(ChatColor.GRAY)
											.append(" entfernt!").color(ChatColor.GREEN).create(),
									true);
							return 0;
						})));
	}

}
