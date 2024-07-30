package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.game.UserQuitGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListeners;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.game.lobby.LobbySidebarTeam;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class LobbyUserQuitGameListener implements ConfiguredListeners {
    private final CommonLobby lobby;
    private final EventNode<Object> node = EventNode.all("quit-game");

    public LobbyUserQuitGameListener(CommonLobby lobby) {
        this.lobby = lobby;
        node.addListener(UserQuitGameEvent.class, this::handle);
        node.addListener(UserQuitGameEvent.Post.class, this::handle);
    }

    private void handle(UserQuitGameEvent event) {
        lobby.quit((CommonWBUser) event.user());
    }

    private void handle(UserQuitGameEvent.Post event) {
        lobby.updateSidebar(LobbySidebarTeam.ONLINE);
    }

    @Override
    public @NotNull EventNode<?> node() {
        return node;
    }
}
