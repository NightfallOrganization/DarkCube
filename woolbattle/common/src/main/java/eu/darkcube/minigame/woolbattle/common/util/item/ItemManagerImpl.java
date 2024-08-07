/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.item;

import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;

class ItemManagerImpl {
    static final ItemManager MANAGER = WoolBattleProvider.PROVIDER.service(ItemManager.class);
}
