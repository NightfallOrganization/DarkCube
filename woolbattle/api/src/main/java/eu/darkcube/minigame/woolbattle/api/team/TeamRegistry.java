package eu.darkcube.minigame.woolbattle.api.team;

import java.util.Collection;

import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

@Api
public interface TeamRegistry {
    String SPECTATOR_KEY = "spectator";

    @Api
    @NotNull @Unmodifiable Collection<? extends TeamConfiguration> teamConfigurations(@NotNull MapSize mapSize);

    @Api
    @NotNull TeamConfiguration createConfiguration(@NotNull MapSize mapSize, @NotNull String key);

    /**
     * Updates the existing configuration with the new one. This basically saves the configuration. This is done, because all TeamConfigurations are copies of internal objects, and have to be set. This approach also helps immutability and integrity.
     *
     * @param configuration the new configuration
     */
    @Api
    void updateConfiguration(@NotNull TeamConfiguration configuration);

    @Api
    @Nullable TeamConfiguration configuration(@NotNull MapSize mapSize, @NotNull String key);

    @Api
    default @Nullable TeamConfiguration spectator(@NotNull MapSize mapSize) {
        return configuration(mapSize, SPECTATOR_KEY);
    }
}
