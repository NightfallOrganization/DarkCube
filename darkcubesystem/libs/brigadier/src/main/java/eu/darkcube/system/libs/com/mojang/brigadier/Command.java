/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier;

import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;

@FunctionalInterface
public interface Command<S> {
    int SINGLE_SUCCESS = 1;

    int run(CommandContext<S> context) throws CommandSyntaxException;
}
