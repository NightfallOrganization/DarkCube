/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.IntegerArgumentType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommandExecutor;
import eu.darkcube.system.lobbysystem.jumpandrun.JaRRegion;

public class CommandDeleteRegion extends LobbyCommandExecutor {

	public CommandDeleteRegion() {
		super("deleteRegion", b -> b.then(Commands.argument("index", IntegerArgumentType.integer(0,
				Lobby.getInstance().getJaRManager().getRegions().size())).executes(ctx -> {
			JaRRegion re = Lobby.getInstance().getJaRManager().getRegions()
					.remove(IntegerArgumentType.getInteger(ctx, "index"));
			Lobby.getInstance().getJaRManager().saveRegions();
			ctx.getSource().sendMessage(Component.text(
							"Region " + IntegerArgumentType.getInteger(ctx, "index") + "entfernt!")
					.color(NamedTextColor.RED));
			return 0;
		})));
	}

}
