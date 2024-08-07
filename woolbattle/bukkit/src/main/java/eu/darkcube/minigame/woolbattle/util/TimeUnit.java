/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

public enum TimeUnit {

    TICKS(50),
    SECOND(1000);

    private final long millis;

    TimeUnit(long millis) {
        this.millis = millis;
    }

    public long toUnit(TimeUnit other) {
        return millis / other.millis;
    }

    public long toMillis() {
        return this.millis;
    }

    public long toTicks() {
        return millis / TICKS.millis;
    }

    public long toTicks(double number) {
        return Math.round((number * (millis / (double) TICKS.millis)));
    }

    public int itoTicks(double number) {
        return (int) toTicks(number);
    }
}
