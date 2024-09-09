/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.map;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Api
public record MapSize(int teams, int teamSize) {
    @Api
    public static @Nullable MapSize fromString(String s) {
        var a = s.split("x");
        if (a.length != 2) return null;
        try {
            var teams = Integer.parseInt(a[0]);
            var teamSize = Integer.parseInt(a[1]);
            return new MapSize(teams, teamSize);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    @Api
    public static @NotNull MapSize parseString(String s) throws IllegalArgumentException {
        var a = s.split("x");
        if (a.length != 2) throw new IllegalArgumentException("Not a valid MapSize: " + s);
        try {
            var teams = Integer.parseInt(a[0]);
            var teamSize = Integer.parseInt(a[1]);
            return new MapSize(teams, teamSize);
        } catch (Throwable t) {
            throw new IllegalArgumentException("Not a valid MapSize: " + s);
        }
    }

    @Override
    public String toString() {
        return teams + "x" + teamSize;
    }
}
