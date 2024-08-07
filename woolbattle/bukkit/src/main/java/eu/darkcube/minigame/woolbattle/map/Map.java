/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.inventory.ItemStack;

public interface Map {

    boolean isEnabled();

    int deathHeight();

    void deathHeight(int height);

    ItemStack getIcon();

    void setIcon(ItemStack icon);

    void enable();

    void disable();

    MapSize size();

    @Nullable
    MapIngameData ingameData();

    String getName();

    String serialize();
}
