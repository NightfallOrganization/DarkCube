/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

public enum GameStates {
    STARTING,
    PLAYING,
    ENDING;

    private static GameStates currentState = STARTING;

    public static void setState(GameStates state) {
        currentState = state;
    }

    public static GameStates getState() {
        return currentState;
    }

    public static boolean isState(GameStates state) {
        return currentState == state;
    }
}
