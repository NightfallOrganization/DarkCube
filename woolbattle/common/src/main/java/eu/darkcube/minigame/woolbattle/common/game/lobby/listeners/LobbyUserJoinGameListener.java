package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.inventory.LobbyUserInventory;

public class LobbyUserJoinGameListener extends ConfiguredListener<UserJoinGameEvent> {
    public LobbyUserJoinGameListener() {
        super(UserJoinGameEvent.class);
    }

    @Override
    public void accept(UserJoinGameEvent event) {
        var inventory = LobbyUserInventory.get(event.user());
        inventory.setAllItems();
    }
}
