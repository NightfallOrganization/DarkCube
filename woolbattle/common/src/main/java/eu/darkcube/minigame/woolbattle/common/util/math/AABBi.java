package eu.darkcube.minigame.woolbattle.common.util.math;

public record AABBi(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
    public AABBi(Vec3i pos, Vec3i size) {
        this(pos.x(), pos.x() + size.x(), pos.y(), pos.y() + size.y(), pos.z(), pos.z() + size.z());
    }

    public boolean intersects(AABBi o) {
        return minX <= o.maxX && maxX >= o.minX && minY <= o.maxY && maxY >= o.minY && minZ <= o.maxZ && maxZ >= o.minZ;
    }

    public boolean containsX(int x) {
        return minX <= x && maxX >= x;
    }

    public boolean containsY(int y) {
        return minY <= y && maxY >= y;
    }

    public boolean containsZ(int z) {
        return minZ <= z && maxZ >= z;
    }

    public boolean contains(Vec3i o) {
        return minX <= o.x() && maxX >= o.x() && minY <= o.y() && maxY >= o.y() && minZ <= o.z() && maxZ >= o.z();
    }

    public boolean contains(int x, int y, int z) {
        return minX <= x && maxX >= x && minY <= y && maxY >= y && minZ <= z && maxZ >= z;
    }
}
