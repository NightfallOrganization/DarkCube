/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world;

import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.world.metadata.CommonWorldBlockMetaDataStorage;
import eu.darkcube.minigame.woolbattle.common.world.metadata.CommonWorldMetaDataStorage;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.MetaDataStorage;

public class CommonWorld implements World {
    protected final CommonGame game;
    private final PlatformWorldHandler platform;
    private final CommonWorldMetaDataStorage metaData = new CommonWorldMetaDataStorage();

    public CommonWorld(CommonGame game) {
        this.game = game;
        this.platform = game.woolbattle().worldHandler();
    }

    @Override
    public @NotNull CommonGame game() {
        return game;
    }

    @Override
    public @NotNull MetaDataStorage metadata() {
        return metaData;
    }

    @Override
    public @NotNull CommonBlock blockAt(int x, int y, int z) {
        return new CommonBlock(this, x, y, z, game.ingameData().maxBlockDamage());
    }

    public PlatformWorldHandler platform() {
        return platform;
    }

    public MetaDataStorage blockMetadata(int x, int y, int z) {
        return new CommonWorldBlockMetaDataStorage(metaData, new CommonWorldBlockMetaDataStorage.BlockKey(x, y, z));
    }
}
