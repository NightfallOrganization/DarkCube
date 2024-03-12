/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.util;

import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;

public interface MaterialProvider {
    boolean isWool(@NotNull Material material);

    @Nullable ColoredWool woolFrom(@NotNull Block block);

    @Nullable ColoredWool woolFrom(@NotNull ItemBuilder item);

    default @NotNull Material air() {
        return Material.air();
    }
}
