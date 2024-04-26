/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

public enum ObjectiveTeam {

    ONLINE("online", "§1§6"),
    NEEDED("needed", "§2§6"),
    MAP("map", "§3§6"),
    TIME("time", "§4§6"),
    EP_GLITCH("ep_glitch", "§5§6"),

    ;

    private final String key;
    private final String invisTag;

    ObjectiveTeam(String key, String invisTag) {
        this.invisTag = invisTag;
        this.key = key;
    }

    public String getInvisTag() {
        return invisTag;
    }

    public String getKey() {
        return key;
    }

    public String getMessagePrefix() {
        return "SCOREBOARD_TEAM_PREFIX_" + name();
    }

    public String getMessageSuffix() {
        return "SCOREBOARD_TEAM_SUFFIX_" + name();
    }
}
