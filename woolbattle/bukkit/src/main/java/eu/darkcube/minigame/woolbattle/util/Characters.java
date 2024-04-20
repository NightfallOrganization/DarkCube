/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

public enum Characters {

    SHIFT_SHIFT_LEFT("«"),
    SHIFT_SHIFT_RIGHT("»"),
    HEART("❤");

    private String c;

    private Characters(String c) {
        this.c = c;
    }

    public String getChar() {
        return c;
    }

    @Override
    public String toString() {
        return getChar();
    }
}
