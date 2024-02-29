/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.util.data.Key;

@Api public interface WoolBattleApi extends Key.Named {
    @Api static WoolBattleApi instance() {
        return WoolBattleApiHolder.instance;
    }

    @Api LobbySystemLink lobbySystemLink();
}
