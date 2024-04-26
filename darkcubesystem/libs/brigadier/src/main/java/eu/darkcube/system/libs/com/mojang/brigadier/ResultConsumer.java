/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier;

import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;

@FunctionalInterface
public interface ResultConsumer<S> {
    void onCommandComplete(CommandContext<S> context, boolean success, int result);
}
