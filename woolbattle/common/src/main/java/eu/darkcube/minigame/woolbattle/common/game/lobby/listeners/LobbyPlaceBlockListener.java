package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.world.block.PlaceBlockEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;

public class LobbyPlaceBlockListener extends ConfiguredListener<PlaceBlockEvent> {
    public LobbyPlaceBlockListener() {
        super(PlaceBlockEvent.class);
    }

    @Override
    public void accept(PlaceBlockEvent event) {
        event.cancel();
    }
}
