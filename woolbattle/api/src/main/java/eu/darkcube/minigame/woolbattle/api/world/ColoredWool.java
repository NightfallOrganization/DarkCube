/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import eu.darkcube.system.server.item.ItemBuilder;

public interface ColoredWool {
    void apply(Block block);

    ItemBuilder createSingleItem();

    String name();
}
