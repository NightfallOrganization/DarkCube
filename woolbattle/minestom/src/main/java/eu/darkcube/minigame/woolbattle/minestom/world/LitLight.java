/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import java.util.Arrays;
import java.util.Set;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.light.Light;
import net.minestom.server.instance.light.LightCompute;

public class LitLight implements Light {
    private static final byte[] FULLY_LIT = new byte[LightCompute.emptyContent.length];

    static {
        Arrays.fill(FULLY_LIT, (byte) 0xFF);
    }

    @Override
    public boolean requiresSend() {
        return true;
    }

    @Override
    public byte[] array() {
        return FULLY_LIT;
    }

    @Override
    public Set<Point> flip() {
        return Set.of();
    }

    @Override
    public Light calculateExternal(Instance instance, Chunk chunk, int sectionY) {
        return this;
    }

    @Override
    public void invalidatePropagation() {
    }

    @Override
    public int getLevel(int x, int y, int z) {
        return 15;
    }

    @Override
    public Light calculateInternal(Instance instance, int chunkX, int chunkY, int chunkZ) {
        return this;
    }

    @Override
    public void invalidate() {
    }

    @Override
    public boolean requiresUpdate() {
        return false;
    }

    @Override
    public void set(byte[] copyArray) {
        throw new UnsupportedOperationException();
    }
}
