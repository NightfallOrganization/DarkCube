package eu.darkcube.minigame.woolbattle.common.game;

import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface ConfiguredListeners {
    @NotNull
    EventNode<?> node();
}
