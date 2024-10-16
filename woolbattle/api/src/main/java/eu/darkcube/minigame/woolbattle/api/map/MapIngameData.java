/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.map;

import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface MapIngameData {
    @NotNull
    Map map();

    @Nullable
    Position.Directed spawn(@NotNull String name);

    void spawn(@NotNull String name, @NotNull Position.Directed position);

    void removeSpawn(@NotNull String name);
}
