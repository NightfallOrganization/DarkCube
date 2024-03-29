/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.minigame.woolbattle.common.world.metadata.CommonWorldBlockMetaDataStorage;
import eu.darkcube.minigame.woolbattle.common.world.metadata.CommonWorldMetaDataStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.MetaDataStorage;

public abstract class CommonWorld implements World {
    private final PlatformWorldHandler platform;
    private final CommonWorldMetaDataStorage metaData = new CommonWorldMetaDataStorage();

    public CommonWorld(@NotNull PlatformWorldHandler worldHandler) {
        this.platform = worldHandler;
    }

    @Override
    public @NotNull MetaDataStorage metadata() {
        return metaData;
    }

    @Override
    public @NotNull CommonBlock blockAt(int x, int y, int z) {
        return new CommonBlock(this, x, y, z);
    }

    @Override
    public void dropAt(double x, double y, double z, @NotNull ColoredWool wool, int count) {
        while (count > 0) {
            var amt = Math.min(64, count);
            count -= amt;
            platform.dropAt(this, x, y, z, wool, amt);
        }
    }

    public PlatformWorldHandler platform() {
        return platform;
    }

    public MetaDataStorage blockMetadata(int x, int y, int z) {
        return new CommonWorldBlockMetaDataStorage(metaData, new CommonWorldBlockMetaDataStorage.BlockKey(x, y, z));
    }
}
