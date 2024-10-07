/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world.impl;

import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;

import java.nio.file.Path;

import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.instance.Instance;

public class MinestomWorldImpl extends CommonWorld implements MinestomWorld {
    private final @NotNull Instance instance;
    private final @Nullable Path worldDirectory;

    public MinestomWorldImpl(MinestomWoolBattle woolbattle, @NotNull PlatformWorldHandler worldHandler, @NotNull Instance instance, @Nullable Path worldDirectory) {
        super(woolbattle, worldHandler);
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

    @Override
    public @NotNull Key key() {
        return adventureSupport().convert(instance.getDimensionType().key());
    }
}
