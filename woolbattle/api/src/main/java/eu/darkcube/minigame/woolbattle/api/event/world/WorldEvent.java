package eu.darkcube.minigame.woolbattle.api.event.world;

import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface WorldEvent {
    @NotNull World world();
}
