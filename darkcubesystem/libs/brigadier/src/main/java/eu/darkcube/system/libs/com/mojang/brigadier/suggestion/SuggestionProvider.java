/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier.suggestion;

import eu.darkcube.system.libs.com.mojang.brigadier.context.CommandContext;
import eu.darkcube.system.libs.com.mojang.brigadier.exceptions.CommandSyntaxException;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface SuggestionProvider<S> {
    CompletableFuture<Suggestions> getSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) throws CommandSyntaxException;
}
