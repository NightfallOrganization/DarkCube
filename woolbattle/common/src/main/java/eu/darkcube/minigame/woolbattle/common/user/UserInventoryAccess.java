/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.user;

import eu.darkcube.system.server.item.ItemBuilder;

public interface UserInventoryAccess {
    void woolCount(int count);

    void setItem(int slot, ItemBuilder item);
}
