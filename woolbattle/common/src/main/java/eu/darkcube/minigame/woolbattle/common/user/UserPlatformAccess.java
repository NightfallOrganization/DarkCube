/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.user;

import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

public interface UserPlatformAccess {
    void woolCount(int count);

    void xp(float xp);

    void setItem(int slot, ItemBuilder item);

    @NotNull
    ItemBuilder itemInHand();

    void playInventorySound();

    void teleport(@NotNull Location location);

    int xpLevel();

    void velocity(@NotNull Vector velocity);

    boolean isAlive();
}
