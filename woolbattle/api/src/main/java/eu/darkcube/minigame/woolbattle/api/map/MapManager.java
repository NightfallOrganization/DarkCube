/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.map;

import java.util.Collection;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

@Api
public interface MapManager {
    @Api
    @Nullable
    Map map(@NotNull String name, @NotNull MapSize mapSize);

    @Api
    @NotNull
    Map createMap(@NotNull String name, @NotNull MapSize mapSize);

    @Api
    @NotNull
    @UnmodifiableView
    Collection<? extends Map> maps();

    @Api
    @NotNull
    @UnmodifiableView
    Collection<? extends Map> maps(@NotNull MapSize mapSize);

    @Api
    void deleteMap(@NotNull Map map);

    @Api
    @NotNull
    MapIngameData loadIngameData(@NotNull Map map);

    @Api
    void saveIngameData(@NotNull MapIngameData ingameData);
}
