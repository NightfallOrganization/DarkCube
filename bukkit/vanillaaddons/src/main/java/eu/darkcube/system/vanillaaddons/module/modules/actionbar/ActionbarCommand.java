/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.module.modules.actionbar;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ActionbarCommand extends Command {
    public ActionbarCommand() {
        super("vanillaaddons", "actionbar", new String[0], b -> b.then(Commands
                .argument("text", StringArgumentType.greedyString())
                .executes(ctx -> {

                    String text = StringArgumentType.getString(ctx, "text");
                    ctx.getSource().asPlayer().sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(text));
                    return 0;
                })));
    }
}
