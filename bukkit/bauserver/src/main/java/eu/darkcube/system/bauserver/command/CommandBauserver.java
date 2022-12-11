/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.command;

import org.bukkit.metadata.FixedMetadataValue;
import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.commandapi.v3.CustomComponentBuilder;

public class CommandBauserver extends CommandExecutor {

	public CommandBauserver() {
		super("bauserver", "bauserver", new String[0], b -> {
			b.then(Commands.literal("togglerain").executes(ctx -> {
				boolean may = !ctx.getSource().asPlayer().getWorld().hasMetadata("mayrain");
				if (may) {
					ctx.getSource().asPlayer().getWorld().setMetadata("mayrain",
							new FixedMetadataValue(Main.getInstance(), true));
				} else {
					ctx.getSource().asPlayer().getWorld().removeMetadata("mayrain",
							Main.getInstance());
				}
				ctx.getSource().sendFeedback(new CustomComponentBuilder("MayRain: " + may).create(),
						true);
				return 0;
			}));
		});
	}

}