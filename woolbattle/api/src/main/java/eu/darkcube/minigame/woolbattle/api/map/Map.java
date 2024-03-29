/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.map;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.server.item.ItemBuilder;

@Api
public interface Map {
    @Api
    boolean enabled();

    @Api
    void enabled(boolean enabled);

    @Api
    int deathHeight();

    @Api
    void deathHeight(int deathHeight);

    @Api
    @NotNull ItemBuilder icon();

    @Api
    void icon(@NotNull ItemBuilder icon);

    @Api
    @NotNull MapSize size();

    @Api
    @NotNull String name();
}
