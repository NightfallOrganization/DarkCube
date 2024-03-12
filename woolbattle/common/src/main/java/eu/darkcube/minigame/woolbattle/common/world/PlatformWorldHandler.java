/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world;

import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonIngameWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.material.Material;

public interface PlatformWorldHandler {
    @NotNull
    Material material(@NotNull CommonWorld world, int x, int y, int z);

    void material(@NotNull CommonWorld world, int x, int y, int z, @NotNull Material material);

    @NotNull
    CommonWorld loadLobbyWorld();

    @NotNull
    CommonIngameWorld loadIngameWorld();

    record LobbyWorld(CommonWorld world) {
    }
}
