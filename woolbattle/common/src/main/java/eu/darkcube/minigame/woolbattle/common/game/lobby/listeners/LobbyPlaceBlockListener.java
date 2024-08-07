/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
