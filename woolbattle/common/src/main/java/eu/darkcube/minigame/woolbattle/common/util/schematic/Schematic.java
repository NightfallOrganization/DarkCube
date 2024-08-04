package eu.darkcube.minigame.woolbattle.common.util.schematic;

import java.util.List;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

public record Schematic(@NotNull SchematicMetadata metadata, @NotNull @Unmodifiable List<SchematicRegion> regions) {
}
