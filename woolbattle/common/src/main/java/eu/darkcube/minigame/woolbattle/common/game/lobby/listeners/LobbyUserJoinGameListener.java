package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class LobbyUserJoinGameListener extends ConfiguredListener<UserJoinGameEvent> {
    private final CommonLobby lobby;

    public LobbyUserJoinGameListener(CommonLobby lobby) {
        super(UserJoinGameEvent.class);
        this.lobby = lobby;
    }

    @Override
    public void accept(UserJoinGameEvent event) {
        this.lobby.join((CommonWBUser) event.user());
    }
}
