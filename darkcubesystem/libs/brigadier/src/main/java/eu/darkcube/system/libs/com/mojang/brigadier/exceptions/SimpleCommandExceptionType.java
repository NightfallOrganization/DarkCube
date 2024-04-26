/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier.exceptions;

import eu.darkcube.system.libs.com.mojang.brigadier.ImmutableStringReader;
import eu.darkcube.system.libs.com.mojang.brigadier.Message;

public class SimpleCommandExceptionType implements CommandExceptionType {
    private final Message message;

    public SimpleCommandExceptionType(final Message message) {
        this.message = message;
    }

    public CommandSyntaxException create() {
        return new CommandSyntaxException(this, message);
    }

    public CommandSyntaxException createWithContext(final ImmutableStringReader reader) {
        return new CommandSyntaxException(this, message, reader.getString(), reader.getCursor());
    }

    @Override
    public String toString() {
        return message.getString();
    }
}
