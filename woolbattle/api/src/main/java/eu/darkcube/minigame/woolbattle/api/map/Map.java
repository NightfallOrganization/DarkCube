/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.map;

import eu.darkcube.system.server.item.ItemBuilder;

public interface Map {
    boolean enabled();

    void enabled(boolean enabled);

    int deathHeight();

    void deathHeight(int deathHeight);

    ItemBuilder icon();

    void icon(ItemBuilder icon);

    MapSize size();

    String name();

}
