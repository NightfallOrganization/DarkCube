/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.schematic;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.nbt.CompoundBinaryTag;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public record SchematicTileEntity(int x, int y, int z, @NotNull Key id, @NotNull @Unmodifiable CompoundBinaryTag components, @NotNull CompoundBinaryTag additionalData) {
    public SchematicTileEntity withPos(int x, int y, int z) {
        return new SchematicTileEntity(x, y, z, id, components, additionalData);
    }
}
