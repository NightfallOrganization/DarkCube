/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.MetaDataStorage;

@Api
public interface World {
    @Api
    @NotNull MetaDataStorage metadata();

    @Api
    @NotNull Block blockAt(int x, int y, int z);

    void dropAt(double x, double y, double z, @NotNull ColoredWool wool, int count);
}
