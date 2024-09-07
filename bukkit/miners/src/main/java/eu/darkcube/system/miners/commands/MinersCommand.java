/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.commands;

import java.util.function.Consumer;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class MinersCommand extends Command {

    public MinersCommand(String name, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("miners", name, aliases, argumentBuilder);
    }

    public MinersCommand(String name, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("miners", name, new String[0], argumentBuilder);
    }

    public MinersCommand(String name, String permission, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("miners", name, permission, aliases, argumentBuilder);
    }

}
