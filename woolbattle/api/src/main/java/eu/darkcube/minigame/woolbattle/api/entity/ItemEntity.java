/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public interface ItemEntity extends Entity {
    @NotNull
    ItemBuilder item();

    void item(@NotNull ItemBuilder item);

    int pickupDelay();

    void pickupDelay(int delay);

    int mergeDelay();

    void mergeDelay(int delay);
}
