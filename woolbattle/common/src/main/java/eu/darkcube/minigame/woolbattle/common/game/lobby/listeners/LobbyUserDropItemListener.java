/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

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
