/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.minestom.event.MinestomInventoryPreClickEvent;

public class MinestomLobbyInventoryClickListener extends ConfiguredListener<MinestomInventoryPreClickEvent> {
    public MinestomLobbyInventoryClickListener() {
        super(MinestomInventoryPreClickEvent.class);
    }

    @Override
    public void accept(MinestomInventoryPreClickEvent customEvent) {
        var event = customEvent.event();
        event.setCancelled(true);
    }
}
