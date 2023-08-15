/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;

public class WoolBattleApiImpl implements WoolBattleApi {
    private final WoolBattleBukkit woolbattle;

    public WoolBattleApiImpl(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        WoolBattleApiHolder.instance = this;
    }

    @Override public LobbySystemLinkImpl lobbySystemLink() {
        return woolbattle.lobbySystemLink();
    }
}
