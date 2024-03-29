/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.util;

import static net.minestom.server.item.Material.GRASS_BLOCK;

import java.util.List;

import eu.darkcube.minigame.woolbattle.api.util.MaterialProvider;
import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomColoredWool;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomColoredWoolProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;

public class MinestomMaterialProvider implements MaterialProvider {
    private final List<Material> wool;

    public MinestomMaterialProvider(MinestomColoredWoolProvider provider) {
        this.wool = provider.woolMaterials();
    }

    @Override
    public boolean isWool(@NotNull Material material) {
        return wool.contains(material);
    }

    @Override
    public @Nullable ColoredWool woolFrom(@NotNull Block block) {
        return new MinestomColoredWool(block.material());
    }

    @Override
    public @Nullable ColoredWool woolFrom(@NotNull ItemBuilder item) {
        return new MinestomColoredWool(item.material());
    }

    @Override
    public @NotNull Material grassBlock() {
        return Material.of(GRASS_BLOCK);
    }
}
