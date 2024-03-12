/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common;

import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerManager;
import eu.darkcube.minigame.woolbattle.common.game.CommonGameManager;
import eu.darkcube.minigame.woolbattle.common.game.GamePhaseCreator;
import eu.darkcube.minigame.woolbattle.common.map.CommonMapManager;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.CommonSchedulerManager;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.SynchronizedPersistentDataStorage;

public abstract class CommonWoolBattleApi implements WoolBattleApi {

    private final @NotNull String databaseNameSuffixMaps;
    private final @NotNull CommonGameManager gameManager;
    private final @NotNull CommonMapManager mapManager;
    private final @NotNull CommonLobbySystemLink lobbySystemLink;
    private final @NotNull SchedulerManager scheduler;
    private final @NotNull PersistentDataStorage persistentDataStorage;

    public CommonWoolBattleApi(@NotNull String databaseNameSuffixMaps) {
        this.databaseNameSuffixMaps = databaseNameSuffixMaps;
        this.gameManager = new CommonGameManager(this);
        this.mapManager = new CommonMapManager(this);
        this.lobbySystemLink = new CommonLobbySystemLink(this);
        this.scheduler = new CommonSchedulerManager();
        this.persistentDataStorage = new SynchronizedPersistentDataStorage(new Key(this, getName()));
    }

    @Override
    public @NotNull PersistentDataStorage persistentDataStorage() {
        return persistentDataStorage;
    }

    @Override
    public @NotNull CommonMapManager mapManager() {
        return mapManager;
    }

    @Override
    public @NotNull CommonLobbySystemLink lobbySystemLink() {
        return lobbySystemLink;
    }

    @Override
    public String getName() {
        return "woolbattle";
    }

    @Override
    public @NotNull SchedulerManager scheduler() {
        return scheduler;
    }

    @Override
    public @NotNull CommonGameManager games() {
        return gameManager;
    }

    public String databaseNameSuffixMaps() {
        return databaseNameSuffixMaps;
    }

    public abstract @NotNull GamePhaseCreator gamePhaseCreator();

    public abstract @NotNull CompletableFuture<Void> fullyLoadedFuture();

    public abstract @NotNull PlatformWorldHandler worldHandler();
}
