/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.command;

import eu.darkcube.system.bauserver.Main;
import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import org.bukkit.metadata.FixedMetadataValue;

public class CommandBauserver extends Command {

    public CommandBauserver() {
        super("bauserver", "bauserver", new String[0], b -> {
            b.then(Commands.literal("togglerain").executes(ctx -> {
                boolean may = !ctx.getSource().asPlayer().getWorld().hasMetadata("mayrain");
                if (may) {
                    ctx.getSource().asPlayer().getWorld().setMetadata("mayrain", new FixedMetadataValue(Main.getInstance(), true));
                } else {
                    ctx.getSource().asPlayer().getWorld().removeMetadata("mayrain", Main.getInstance());
                }
                ctx.getSource().sendMessage(Component.text("MayRain: " + may));
                return 0;
            }));
        });
    }

}
