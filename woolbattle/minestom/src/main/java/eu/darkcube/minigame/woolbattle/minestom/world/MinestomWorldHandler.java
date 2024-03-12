/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonIngameWorld;
import eu.darkcube.minigame.woolbattle.common.world.CommonWorld;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.material.Material;
import net.minestom.server.instance.Instance;

public class MinestomWorldHandler implements PlatformWorldHandler {
    @Override
    public @NotNull Material material(@NotNull CommonWorld world, int x, int y, int z) {
        return Material.ofNullable(instance(world).getBlock(x, y, z).registry().material());
    }

    @Override
    public void material(@NotNull CommonWorld world, int x, int y, int z, @NotNull Material material) {
        var minestomType = ((MinestomMaterial) material).minestomType();
        instance(world).setBlock(x, y, z, minestomType.block());
    }

    @Override
    public @NotNull CommonWorld loadLobbyWorld() {
        return null;
    }

    @Override
    public @NotNull CommonIngameWorld loadIngameWorld() {
        return null;
    }

    private Instance instance(CommonWorld world) {
        return ((MinestomWorld) world).instance();
    }
}
