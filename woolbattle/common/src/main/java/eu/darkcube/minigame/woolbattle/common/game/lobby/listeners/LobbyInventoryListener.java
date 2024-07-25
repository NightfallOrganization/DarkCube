package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserChangeTeamEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListeners;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class LobbyInventoryListener implements ConfiguredListeners {
    private final EventNode<Object> node;

    public LobbyInventoryListener(CommonLobby lobby) {
        this.node = EventNode.all("lobby-inventory");
        this.node.addListener(UserChangeTeamEvent.class, _ -> lobby.teamsInventoryTemplate().pagination().content().publishUpdateAll());
    }

    @Override
    public @NotNull EventNode<?> node() {
        return node;
    }
}
