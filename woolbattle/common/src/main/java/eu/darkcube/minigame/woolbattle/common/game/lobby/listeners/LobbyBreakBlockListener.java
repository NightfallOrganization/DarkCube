/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
