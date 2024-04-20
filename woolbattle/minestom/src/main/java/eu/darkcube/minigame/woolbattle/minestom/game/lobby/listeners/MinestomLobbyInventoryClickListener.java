package eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.minestom.event.MinestomInventoryClickEvent;

public class MinestomLobbyInventoryClickListener extends ConfiguredListener<MinestomInventoryClickEvent> {
    public MinestomLobbyInventoryClickListener() {
        super(MinestomInventoryClickEvent.class);
    }

    @Override
    public void accept(MinestomInventoryClickEvent customEvent) {
        var event = customEvent.event();
        event.setCancelled(true);
    }
}
