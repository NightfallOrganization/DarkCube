package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserParticlesUpdateEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.inventory.LobbyUserInventory;

public class LobbyUserParticlesUpdateListener extends ConfiguredListener<UserParticlesUpdateEvent> {
    public LobbyUserParticlesUpdateListener() {
        super(UserParticlesUpdateEvent.class);
    }

    @Override
    public void accept(UserParticlesUpdateEvent event) {
        LobbyUserInventory.get(event.user()).setParticlesItem();
    }
}
