/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

public class MapSize {
    private final int teams;
    private final int teamSize;

    public MapSize(int teams, int teamSize) {
        this.teams = teams;
        this.teamSize = teamSize;
    }

    public int teams() {
        return teams;
    }

    public int teamSize() {
        return teamSize;
    }

    @Override
    public String toString() {
        return teams + "x" + teamSize;
    }
}
