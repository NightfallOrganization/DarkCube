/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world;

import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.data.MetaDataStorage;
import org.jetbrains.annotations.Contract;

public class CommonBlock implements Block {
    private final CommonWorld world;
    protected final int x;
    protected final int y;
    protected final int z;
    protected final int maxBlockDamage;
    protected final Location location;

    public CommonBlock(CommonWorld world, int x, int y, int z, int maxBlockDamage) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.maxBlockDamage = maxBlockDamage;
        this.location = new Location(world, x, y, z);
    }

    @Contract(pure = true)
    @Override
    public final int x() {
        return x;
    }

    @Contract(pure = true)
    @Override
    public final int y() {
        return y;
    }

    @Contract(pure = true)
    @Override
    public final int z() {
        return z;
    }

    @Override
    public @NotNull MetaDataStorage metadata() {
        return world.blockMetadata(x, y, z);
    }

    @Override
    public @NotNull CommonWorld world() {
        return world;
    }

    @Override
    public @NotNull Location location() {
        return location;
    }

    @Override
    public @NotNull Material material() {
        return world.platform().material(world, x, y, z);
    }

    @Override
    public void material(@NotNull Material material) {
        world.platform().material(world, x, y, z, material);
    }

    @Override
    public int blockDamage() {
        return 0;
    }

    @Override
    public void incrementBlockDamage() {
        incrementBlockDamage(1);
    }

    @Override
    public void incrementBlockDamage(int amount) {
    }
}
