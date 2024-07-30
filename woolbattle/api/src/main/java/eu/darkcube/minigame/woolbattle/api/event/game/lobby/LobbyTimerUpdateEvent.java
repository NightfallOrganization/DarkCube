package eu.darkcube.minigame.woolbattle.api.event.game.lobby;

import java.time.Duration;

import eu.darkcube.minigame.woolbattle.api.event.game.GameEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class LobbyTimerUpdateEvent extends GameEvent.Event {
    private final @NotNull Duration newTimer;

    public LobbyTimerUpdateEvent(@NotNull Game game, @NotNull Duration newTimer) {
        super(game);
        this.newTimer = newTimer;
    }

    public @NotNull Duration newTimer() {
        return newTimer;
    }
}
