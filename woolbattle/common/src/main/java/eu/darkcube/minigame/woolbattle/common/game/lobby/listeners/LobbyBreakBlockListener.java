package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.world.block.BreakBlockEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;

public class LobbyBreakBlockListener extends ConfiguredListener<BreakBlockEvent> {
    public LobbyBreakBlockListener() {
        super(BreakBlockEvent.class);
    }

    @Override
    public void accept(BreakBlockEvent event) {
        event.cancel();
    }
}
