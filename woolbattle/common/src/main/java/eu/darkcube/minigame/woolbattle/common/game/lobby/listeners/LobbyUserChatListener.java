/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game.lobby.listeners;

import eu.darkcube.minigame.woolbattle.api.event.user.UserChatEvent;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattle;
import eu.darkcube.minigame.woolbattle.common.game.ConfiguredListener;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;

public class LobbyUserChatListener extends ConfiguredListener<UserChatEvent> {
    private final CommonWoolBattle woolbattle;

    public LobbyUserChatListener(CommonWoolBattle woolbattle) {
        super(UserChatEvent.class);
        this.woolbattle = woolbattle;
    }

    @Override
    public void accept(UserChatEvent event) {
        var user = (CommonWBUser) event.user();
        var message = event.message();
        woolbattle.chatHandler().lobbyChat(user, message);
    }
}
