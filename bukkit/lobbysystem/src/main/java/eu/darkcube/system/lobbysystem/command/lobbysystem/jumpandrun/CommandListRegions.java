/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.command.lobbysystem.jumpandrun;

import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.LobbyCommand;
import eu.darkcube.system.lobbysystem.jumpandrun.JaRRegion;

public class CommandListRegions extends LobbyCommand {

	public CommandListRegions() {
		super("listRegions", b -> b.executes(ctx -> {
			Component ccb = Component.text("Regions: ").color(NamedTextColor.GRAY)
					.append(Component.text(Integer.toString(
									Lobby.getInstance().getJaRManager().getRegions().size()))
							.color(NamedTextColor.GOLD));
			int i = 0;
			for (JaRRegion r : Lobby.getInstance().getJaRManager().getRegions()) {
				ccb = ccb.append(Component.text("\n - " + i + ": ").color(NamedTextColor.YELLOW))
						.append(Component.text(r.toString()).color(NamedTextColor.GOLD));
				i++;
			}
			ctx.getSource().sendMessage(ccb);
			return 0;
		}));
	}

}
