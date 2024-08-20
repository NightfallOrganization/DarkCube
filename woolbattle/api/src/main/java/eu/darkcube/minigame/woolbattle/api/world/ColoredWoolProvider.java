/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import java.util.Collection;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.server.item.material.Material;

@Api
public interface ColoredWoolProvider {
    @Api
    @NotNull
    ColoredWool defaultWool();

    @Api
    @NotNull
    String serializeToString(@NotNull ColoredWool wool);

    @Api
    @NotNull
    ColoredWool deserializeFromString(@NotNull String serialized);

    @Api
    @Nullable
    ColoredWool woolFrom(@NotNull Material material);

    @Api
    @NotNull
    @Unmodifiable
    Collection<? extends ColoredWool> woolColors();
}
