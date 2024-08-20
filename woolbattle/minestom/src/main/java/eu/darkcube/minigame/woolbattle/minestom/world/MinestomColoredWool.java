/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.minigame.woolbattle.common.game.ingame.world.CommonIngameBlock;
import eu.darkcube.minigame.woolbattle.common.world.CommonColoredWool;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.minestom.item.material.MinestomMaterial;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.material.Material;

public record MinestomColoredWool(@NotNull Material material) implements CommonColoredWool {
    @Override
    public void apply(Block block) {
        if (block instanceof CommonIngameBlock ingame) {
            ingame.regenerateTo(this);
        } else {
            throw new IllegalStateException("Block is not a CommonIngameBlock");
        }
    }

    @Override
    public void unsafeApply(Block block) {
        block.material(material);
    }

    @Override
    public ItemBuilder createSingleItem() {
        return ItemBuilder.item(material);
    }

    @Override
    public String name() {
        var name = ((MinestomMaterial) material).minestomType().registry().namespace().value();
        if (name.endsWith("_wool")) {
            return name.substring(0, name.length() - 5);
        }
        return name;
    }
}
