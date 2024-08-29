/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserMoveEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;

public class LobbyUserMoveListener extends ConfiguredListener<UserMoveEvent> {
    private final CommonLobby lobby;

    public LobbyUserMoveListener(CommonLobby lobby) {
        super(UserMoveEvent.class);
        this.lobby = lobby;
    }

    @Override
    public void accept(UserMoveEvent event) {
        if (event.location().y() < lobby.game().lobbyData().deathLine()) {
            event.user().teleport(lobby.spawn());
        }
    }
}
