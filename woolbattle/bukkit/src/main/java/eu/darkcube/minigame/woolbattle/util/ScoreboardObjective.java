/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

public enum ScoreboardObjective {

    LOBBY("lobby"), INGAME("ingame"), ENDGAME("endgame"),

    ;

    private final String key;

    ScoreboardObjective(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getMessageKey() {
        return "SCOREBOARD_OBJECTIVE_" + name();
    }
}
