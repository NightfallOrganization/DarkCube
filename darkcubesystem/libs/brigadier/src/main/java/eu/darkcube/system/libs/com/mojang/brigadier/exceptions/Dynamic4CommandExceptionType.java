/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier.exceptions;

import eu.darkcube.system.libs.com.mojang.brigadier.ImmutableStringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.Message;

public class Dynamic4CommandExceptionType implements CommandExceptionType {
    private final Function function;

    public Dynamic4CommandExceptionType(final Function function) {
        this.function = function;
    }

    public CommandSyntaxException create(final Object a, final Object b, final Object c, final Object d) {
        return new CommandSyntaxException(this, function.apply(a, b, c, d));
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader, final Object a, final Object b, final Object c, final Object d) {
        return new CommandSyntaxException(this, function.apply(a, b, c, d), reader.getString(), reader.getCursor());
    }

    public interface Function {
        Message apply(Object a, Object b, Object c, Object d);
    }
}
