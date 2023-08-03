/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.vanillaaddons.module.modules.actionbar;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.libs.com.mojang.brigadier.arguments.StringArgumentType;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class ActionbarCommand extends CommandExecutor {

    private static final char overlay = (char) 0x2C92;
    private static final Char2IntMap sizes = new Char2IntOpenHashMap();

    static {
        sizes.put(overlay, 257);

    }

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
