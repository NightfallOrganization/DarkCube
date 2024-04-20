/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.command.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;
import eu.darkcube.system.pserver.plugin.PServerPlugin;

public abstract class PServer extends Command {

    public static final Collection<String> COMMAND_NAMES = new HashSet<>();
    public static final Collection<String> TOTAL_COMMAND_NAMES = new HashSet<>();

    public PServer(String name, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super(PServerPlugin.COMMAND_PREFIX, name, PServerPlugin.COMMAND_PREFIX + "." + name, aliases, argumentBuilder);
        COMMAND_NAMES.add(name);
        for (String n : getNames()) {
            TOTAL_COMMAND_NAMES.add(n);
            TOTAL_COMMAND_NAMES.add(PServerPlugin.COMMAND_PREFIX + ":" + n);
        }
    }
}
