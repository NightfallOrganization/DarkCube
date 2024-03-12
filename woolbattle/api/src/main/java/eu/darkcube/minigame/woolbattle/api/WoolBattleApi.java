/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import eu.darkcube.minigame.woolbattle.api.entity.EntityImplementations;
import eu.darkcube.minigame.woolbattle.api.game.GameManager;
import eu.darkcube.minigame.woolbattle.api.map.MapManager;
import eu.darkcube.minigame.woolbattle.api.util.MaterialProvider;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerManager;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;

@Api
public interface WoolBattleApi extends Key.Named {
    @Api
    static @NotNull WoolBattleApi instance() {
        return WoolBattleApiImpl.instance;
    }

    @Api
    @NotNull
    GameManager games();

    @Api
    @NotNull
    LobbySystemLink lobbySystemLink();

    @Api
    @NotNull
    EntityImplementations entityImplementations();

    @Api
    @NotNull
    MaterialProvider materialProvider();

    @Api
    @NotNull
    MapManager mapManager();

    @Api
    @NotNull
    SchedulerManager scheduler();

    @Api
    @NotNull
    PersistentDataStorage persistentDataStorage();
}
