/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world.metadata;

import eu.darkcube.minigame.woolbattle.common.util.CommonRedirectMetaDataStorage;

public class CommonWorldBlockMetaDataStorage extends CommonRedirectMetaDataStorage<CommonWorldBlockMetaDataStorage.BlockKey> {

    public CommonWorldBlockMetaDataStorage(CommonWorldMetaDataStorage metaDataStorage, BlockKey key) {
        super(metaDataStorage.blockMetaMap, key);
    }

    public record BlockKey(int x, int y, int z) {
    }
}
