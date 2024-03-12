/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world.metadata;

import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.system.util.data.BasicMetaDataStorage;

public class CommonWorldMetaDataStorage extends BasicMetaDataStorage {
    final ConcurrentHashMap<CommonWorldBlockMetaDataStorage.BlockKey, BasicMetaDataStorage> blockMetaMap = new ConcurrentHashMap<>();
}
