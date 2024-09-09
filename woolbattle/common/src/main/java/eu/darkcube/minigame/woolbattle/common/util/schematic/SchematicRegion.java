/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.schematic;

import eu.darkcube.minigame.woolbattle.common.util.math.Vec3i;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record SchematicRegion(@NotNull String name, @NotNull Vec3i position, @NotNull Vec3i size, @NotNull SchematicBlockPalette blockPalette, @NotNull SchematicBlockBitArray blockBitArray, @NotNull SchematicTileEntities tileEntities) {
}
