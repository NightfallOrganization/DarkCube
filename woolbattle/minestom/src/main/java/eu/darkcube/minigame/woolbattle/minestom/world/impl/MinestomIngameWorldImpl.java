/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world.impl;

import java.nio.file.Path;

import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.world.CommonIngameWorld;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.instance.Instance;

public class MinestomIngameWorldImpl extends CommonIngameWorld implements MinestomWorld {
    private final @NotNull Instance instance;
    private final @Nullable Path worldDirectory;

    public MinestomIngameWorldImpl(@NotNull CommonGame game, @NotNull Instance instance, @Nullable Path worldDirectory) {
        super(game);
        this.instance = instance;
        this.worldDirectory = worldDirectory;
    }

    @Override
    public @NotNull Instance instance() {
        return instance;
    }

    @Override
    public @Nullable Path worldDirectory() {
        return worldDirectory;
    }
}
