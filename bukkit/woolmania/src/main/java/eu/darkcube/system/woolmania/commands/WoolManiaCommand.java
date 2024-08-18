/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.commands;

import java.util.function.Consumer;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class WoolManiaCommand extends Command {

    public WoolManiaCommand(String name, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("woolmania", name, aliases, argumentBuilder);
    }

    public WoolManiaCommand(String name, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("woolmania", name, new String[0], argumentBuilder);
    }

    public WoolManiaCommand(String name, String permission, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("woolmania", name, permission, aliases, argumentBuilder);
    }

}
