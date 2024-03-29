/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

public enum WoolSubtractDirection {

    LEFT_TO_RIGHT,
    RIGHT_TO_LEFT;

    public static WoolSubtractDirection getDefault() {
        return RIGHT_TO_LEFT;
    }

}
