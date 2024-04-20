package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.item.UserDropItemEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;

public class LobbyUserDropItemListener extends ConfiguredListener<UserDropItemEvent> {
    public LobbyUserDropItemListener() {
        super(UserDropItemEvent.class);
    }

    @Override
    public void accept(UserDropItemEvent event) {
        event.cancel();
    }
}
