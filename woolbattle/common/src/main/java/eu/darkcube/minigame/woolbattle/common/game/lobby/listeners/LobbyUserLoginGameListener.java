/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.game.UserLoginGameEvent;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class LobbyUserLoginGameListener extends ConfiguredListener<UserLoginGameEvent> {
    private final @NotNull CommonLobby lobby;

    public LobbyUserLoginGameListener(@NotNull CommonLobby lobby) {
        super(UserLoginGameEvent.class);
        this.lobby = lobby;
    }

    @Override
    public void accept(UserLoginGameEvent event) {
        event.result(UserLoginGameEvent.Result.USER_PLAYING);
        event.spawnLocation(lobby.spawn());
        lobby.preJoin((CommonWBUser) event.user());
    }
}
