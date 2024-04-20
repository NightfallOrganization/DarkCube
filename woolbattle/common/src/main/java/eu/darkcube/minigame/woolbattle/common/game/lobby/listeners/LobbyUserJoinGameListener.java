package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.game.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class LobbyUserJoinGameListener extends ConfiguredListener<UserJoinGameEvent> {
    private final @NotNull CommonLobby lobby;

    public LobbyUserJoinGameListener(@NotNull CommonLobby lobby) {
        super(UserJoinGameEvent.class);
        this.lobby = lobby;
    }

    @Override
    public void accept(UserJoinGameEvent event) {
        event.result(UserJoinGameEvent.Result.USER_PLAYING);
        event.spawnLocation(lobby.spawn());
    }
}
