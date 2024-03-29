package eu.darkcube.minigame.woolbattle.api.team;

import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWool;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.Style;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface TeamConfiguration extends Cloneable {
    @Api
    @NotNull String key();

    @Api
    @NotNull MapSize mapSize();

    @Api
    @NotNull Style nameStyle();

    @Api
    void nameStyle(@NotNull Style style);

    @Api
    @NotNull ColoredWool woolColor();

    @Api
    void woolColor(@NotNull ColoredWool wool);

    @Api
    @NotNull TeamType type();

    @NotNull TeamConfiguration clone();
}
