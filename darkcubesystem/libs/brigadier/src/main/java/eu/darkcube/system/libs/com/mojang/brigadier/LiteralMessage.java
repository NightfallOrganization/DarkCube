/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.libs.com.mojang.brigadier;

public class LiteralMessage implements Message {
    private final String string;

    public LiteralMessage(final String string) {
        this.string = string;
    }

    @Override
    public String getString() {
        return string;
    }

    @Override
    public String toString() {
        return string;
    }
}
