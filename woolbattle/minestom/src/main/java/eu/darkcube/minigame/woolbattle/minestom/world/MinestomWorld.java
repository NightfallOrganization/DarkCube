/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import java.nio.file.Path;

import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.minigame.woolbattle.common.world.CommonBlock;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.instance.Instance;

public interface MinestomWorld extends World {
    @NotNull Instance instance();

    @Nullable Path worldDirectory();

    @Override
    @NotNull CommonBlock blockAt(int x, int y, int z);
}
