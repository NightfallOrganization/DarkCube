/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.commandapi.v3;

import eu.darkcube.system.libs.com.mojang.brigadier.builder.LiteralArgumentBuilder;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

public class CommandExecutor {

    private final String name;
    private final String prefix;
    private final String permission;
    private final String[] aliases;
    private final String[] names;
    private final Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder;

    public CommandExecutor(String prefix, String name, String[] aliases,
                           Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        this(prefix, name, prefix + "." + name, aliases, argumentBuilder);
    }

    public CommandExecutor(String prefix, String name, String permission, String[] aliases,
                           Consumer<LiteralArgumentBuilder<CommandSource>> argumentBuilder) {
        this.prefix = prefix;
        this.name = name.toLowerCase();
        this.permission = permission;
        this.aliases = aliases.clone();
        this.argumentBuilder = argumentBuilder;
        this.names = new String[this.aliases.length + 1];
        this.names[0] = this.name;
        for (int i = 0; i < this.aliases.length; i++) {
            this.aliases[i] = this.aliases[i].toLowerCase(Locale.ENGLISH);
        }
        System.arraycopy(this.aliases, 0, this.names, 1, this.names.length - 1);
    }

    public final LiteralArgumentBuilder<CommandSource> builder() {
        return builder(name);
    }

    public final LiteralArgumentBuilder<CommandSource> builder(String name) {
        LiteralArgumentBuilder<CommandSource> b = Commands.literal(name);
        argumentBuilder.accept(b);
        return b;
    }

    public String[] getAliases() {
        return aliases.clone();
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getPermission() {
        return permission;
    }

    public String[] getNames() {
        return names.clone();
    }

    @Override
    public String toString() {
        return "CommandExecutor{" + "name='" + name + '\'' + ", prefix='" + prefix + '\''
                + ", permission='" + permission + '\'' + ", aliases=" + Arrays.toString(aliases)
                + '}';
    }
}
