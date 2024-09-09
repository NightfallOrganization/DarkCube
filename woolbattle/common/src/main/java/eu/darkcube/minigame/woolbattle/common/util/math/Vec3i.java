package eu.darkcube.minigame.woolbattle.common.util.math;

import static java.lang.Math.max;
import static java.lang.Math.min;

public record Vec3i(int x, int y, int z) {
    public Vec3i relativeEndPos() {
        return new Vec3i(x >= 0 ? x - 1 : x + 1, y >= 0 ? y - 1 : y + 1, z >= 0 ? z - 1 : z + 1);
    }

    public Vec3i minCorner(Vec3i o) {
        return new Vec3i(min(x, o.x), min(y, o.y), min(z, o.z));
    }

    public Vec3i maxCorner(Vec3i o) {
        return new Vec3i(max(x, o.x), max(y, o.y), max(z, o.z));
    }

    public Vec3i sub(Vec3i o) {
        return new Vec3i(x - o.x, y - o.y, z - o.z);
    }

    public Vec3i add(Vec3i o) {
        return new Vec3i(x + o.x, y + o.y, z + o.z);
    }

    public Vec3i add(int x, int y, int z) {
        return new Vec3i(this.x + x, this.y + y, this.z + z);
    }
}
