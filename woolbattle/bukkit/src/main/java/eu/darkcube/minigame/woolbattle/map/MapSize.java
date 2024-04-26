/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import java.util.Objects;

public class MapSize {
    private final int teams;
    private final int teamSize;

    public MapSize(int teams, int teamSize) {
        this.teams = teams;
        this.teamSize = teamSize;
    }

    public static MapSize fromString(String s) {
        String[] a = s.split("x");
        if (a.length != 2) return null;
        try {
            int teams = Integer.parseInt(a[0]);
            int teamSize = Integer.parseInt(a[1]);
            return new MapSize(teams, teamSize);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MapSize mapSize = (MapSize) o;
        return teams == mapSize.teams && teamSize == mapSize.teamSize;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teams, teamSize);
    }
}
