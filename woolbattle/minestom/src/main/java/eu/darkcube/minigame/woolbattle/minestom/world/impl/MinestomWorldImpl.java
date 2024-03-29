/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world.impl;

import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.instance.Instance;

public class MinestomWorldImpl extends CommonWorld implements MinestomWorld {
    private final @NotNull Instance instance;

    public MinestomWorldImpl(@NotNull PlatformWorldHandler worldHandler, @NotNull Instance instance) {
        super(worldHandler);
        this.instance = instance;
    }

    @Override
    public @NotNull Instance instance() {
        return instance;
    }
}
