/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.world;

import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonGameWorld;
import eu.darkcube.minigame.woolbattle.common.util.schematic.Schematic;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.material.Material;

public interface PlatformWorldHandler {
    @NotNull
    Material material(@NotNull CommonWorld world, int x, int y, int z);

    void material(@NotNull CommonWorld world, int x, int y, int z, @NotNull Material material);

    @NotNull
    CommonWorld loadSetupWorld();

    @NotNull
    CommonGameWorld loadLobbyWorld(@NotNull CommonGame game);

    @NotNull
    CommonIngameWorld loadIngameWorld(@NotNull CommonGame game, @Nullable Schematic schematic);

    void dropAt(@NotNull CommonWorld world, double x, double y, double z, @NotNull ColoredWool wool, int amt);

    void unloadWorld(@NotNull CommonWorld world);
}
