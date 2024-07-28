package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.game.UserQuitGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.inventory.LobbyUserInventory;

public class LobbyUserQuitGameListener extends ConfiguredListener<UserQuitGameEvent> {
    public LobbyUserQuitGameListener() {
        super(UserQuitGameEvent.class);
    }

    @Override
    public void accept(UserQuitGameEvent event) {
        LobbyUserInventory.destroy(event.user());
    }
}
