/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.command.arguments.entity;

public class Mth {
    public static double wrapDegrees(double degrees) {
        var d = degrees % 360.0;
        if (d >= 180.0) {
            d -= 360.0;
        }

        if (d < -180.0) {
            d += 360.0;
        }

        return d;
    }
}
