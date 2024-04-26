/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier.exceptions;

import eu.darkcube.system.libs.com.mojang.brigadier.ImmutableStringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.Message;

public class DynamicNCommandExceptionType implements CommandExceptionType {
    private final Function function;

    public DynamicNCommandExceptionType(final Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(final Object a, final Object... args) {
        return new CommandSyntaxException(this, function.apply(args));
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader, final Object... args) {
        return new CommandSyntaxException(this, function.apply(args), reader.getString(), reader.getCursor());
    }

    public interface Function {
        Message apply(Object[] args);
    }
}
