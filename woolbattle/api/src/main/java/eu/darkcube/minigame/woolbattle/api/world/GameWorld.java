package eu.darkcube.minigame.woolbattle.api.world;

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

@Api
public interface GameWorld extends World {
    @Api
    @NotNull Game game();
}
