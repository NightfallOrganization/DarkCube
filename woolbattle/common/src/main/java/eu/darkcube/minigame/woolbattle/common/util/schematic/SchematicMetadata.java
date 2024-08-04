package eu.darkcube.minigame.woolbattle.common.util.schematic;

import eu.darkcube.minigame.woolbattle.common.util.math.Vec3i;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record SchematicMetadata(@NotNull String name, int totalBlocks, @NotNull Vec3i enclosingSize) {
}
