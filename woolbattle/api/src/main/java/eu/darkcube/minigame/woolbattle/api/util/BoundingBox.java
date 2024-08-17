/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import eu.darkcube.minigame.woolbattle.api.world.Position;

public record BoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
    public BoundingBox(Position min, Position max) {
        this(min.x(), min.y(), min.z(), max.x(), max.y(), max.z());
    }
}
