package eu.darkcube.minigame.woolbattle.api.world;

import java.util.Collection;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

@Api
public interface ColoredWoolProvider {
    @Api
    @NotNull ColoredWool defaultWool();

    @Api
    @NotNull String serializeToString(@NotNull ColoredWool wool);

    @Api
    @NotNull ColoredWool deserializeFromString(@NotNull String serialized);

    @Api
    @NotNull @Unmodifiable Collection<? extends ColoredWool> woolColors();
}
