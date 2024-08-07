/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;
import eu.darkcube.system.util.data.MetaDataStorage;

@Api
public interface Block {
    @Api
    int x();

    @Api
    int y();

    @Api
    int z();

    @Api
    @NotNull
    World world();

    @Api
    @NotNull
    Location location();

    @Api
    @NotNull
    Material material();

    @Api
    void material(@NotNull Material material);

    @Api
    @NotNull
    MetaDataStorage metadata();

    @Api
    int blockDamage();

    @Api
    void incrementBlockDamage();

    @Api
    void incrementBlockDamage(int amount);
}
