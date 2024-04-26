/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier;

import eu.darkcube.system.libs.com.mojang.brigadier.tree.CommandNode;

import java.util.Collection;

@FunctionalInterface
public interface AmbiguityConsumer<S> {
    void ambiguous(final CommandNode<S> parent, final CommandNode<S> child, final CommandNode<S> sibling, final Collection<String> inputs);
}
