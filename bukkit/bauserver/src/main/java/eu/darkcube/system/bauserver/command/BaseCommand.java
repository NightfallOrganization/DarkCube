/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.bauserver.command;

import java.util.function.Consumer;

import eu.darkcube.system.bukkit.commandapi.Command;
import eu.darkcube.system.bukkit.commandapi.CommandSource;
import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

public class BaseCommand extends Command {
    public BaseCommand(String name, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        this(name, new String[0], argumentBuilder);
    }

    public BaseCommand(String name, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("bauserver", name, aliases, argumentBuilder);
    }

    public BaseCommand(String name, String[] aliases, String description, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("bauserver", name, aliases, description, argumentBuilder);
    }

    public BaseCommand(String name, String permission, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("bauserver", name, permission, aliases, argumentBuilder);
    }

    public BaseCommand(String name, String permission, String description, String[] aliases, Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        super("bauserver", name, permission, description, aliases, argumentBuilder);
    }
}
