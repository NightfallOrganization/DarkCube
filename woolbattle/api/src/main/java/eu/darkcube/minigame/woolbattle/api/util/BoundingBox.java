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

    public BoundingBox move(Position vec) {
        return this.move(vec.x(), vec.y(), vec.z());
    }

    public BoundingBox move(double x, double y, double z) {
        return new BoundingBox(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
    }

    public boolean intersects(BoundingBox box) {
        return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
    }
}
