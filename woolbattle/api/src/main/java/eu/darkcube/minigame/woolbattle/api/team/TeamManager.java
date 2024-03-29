package eu.darkcube.minigame.woolbattle.api.team;

import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

@Api
public interface TeamManager {
    @Api
    @NotNull Game game();

    @Api
    @Nullable Team team(UUID uniqueId);
}
