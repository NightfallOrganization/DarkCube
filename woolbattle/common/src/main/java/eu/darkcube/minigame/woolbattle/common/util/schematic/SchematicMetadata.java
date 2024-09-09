/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util.schematic;

import eu.darkcube.minigame.woolbattle.common.util.math.Vec3i;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record SchematicMetadata(@NotNull String name, int totalBlocks, int totalVolume, int regionCount, @NotNull Vec3i enclosingSize) {
}
