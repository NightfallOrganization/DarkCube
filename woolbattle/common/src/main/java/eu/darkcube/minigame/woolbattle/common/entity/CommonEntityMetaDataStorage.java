/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.entity;

import java.util.Map;

import eu.darkcube.minigame.woolbattle.common.util.CommonRedirectMetaDataStorage;
import eu.darkcube.system.util.data.BasicMetaDataStorage;

public class CommonEntityMetaDataStorage extends CommonRedirectMetaDataStorage<String> {
    public CommonEntityMetaDataStorage(Map<String, BasicMetaDataStorage> map, String s) {
        super(map, s);
    }
}
