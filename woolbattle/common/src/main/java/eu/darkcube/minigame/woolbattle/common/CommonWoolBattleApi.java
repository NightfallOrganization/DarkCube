/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common;

import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.command.CommonWoolBattleCommands;
import eu.darkcube.minigame.woolbattle.common.command.arguments.CommonWoolBattleArguments;
import eu.darkcube.minigame.woolbattle.common.game.CommonGameManager;
import eu.darkcube.minigame.woolbattle.common.game.GamePhaseCreator;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobbyData;
import eu.darkcube.minigame.woolbattle.common.map.CommonMapManager;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeamRegistry;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.item.ItemManager;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.CommonSchedulerManager;
import eu.darkcube.minigame.woolbattle.common.world.PlatformWorldHandler;
import eu.darkcube.minigame.woolbattle.provider.WoolBattleProvider;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PluginPersistentDataProvider;

public abstract class CommonWoolBattleApi implements WoolBattleApi {

    private final @NotNull String databaseNameSuffixMaps;
    private final @NotNull CommonGameManager gameManager;
    private final @NotNull CommonMapManager mapManager;
    private final @NotNull CommonLobbySystemLink lobbySystemLink;
    private final @NotNull CommonTeamRegistry teamRegistry;
    private final @NotNull CommonSchedulerManager scheduler;
    private final @NotNull CommonLobbyData lobbyData;
    private final @NotNull PersistentDataStorage persistentDataStorage;
    private final @NotNull EventNode<Object> eventManager;
    private final @NotNull CommonWoolBattleArguments commandArguments;

    public CommonWoolBattleApi(@NotNull String databaseNameSuffixMaps) {
        this.databaseNameSuffixMaps = databaseNameSuffixMaps;
        this.gameManager = new CommonGameManager(this);
        this.mapManager = new CommonMapManager(this);
        this.lobbySystemLink = new CommonLobbySystemLink(this);
        this.scheduler = new CommonSchedulerManager();
        this.persistentDataStorage = PluginPersistentDataProvider.pluginPersistentDataProvider().persistentData(Key.key(this, "woolbattle"));
        this.teamRegistry = new CommonTeamRegistry(this);
        this.eventManager = EventNode.all("woolbattle");
        this.lobbyData = new CommonLobbyData(this);
        this.commandArguments = new CommonWoolBattleArguments();
        WoolBattleProvider.PROVIDER.register(ItemManager.class, new ItemManager(this));
    }

    @Override
    public @NotNull String namespace() {
        return "woolbattle";
    }

    @Override
    public @NotNull CommonWoolBattleArguments commandArguments() {
        return commandArguments;
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
    public @NotNull CommonSchedulerManager scheduler() {
        return scheduler;
    }

    @Override
    public @NotNull CommonGameManager games() {
        return gameManager;
    }

    @Override
    public @NotNull EventNode<Object> eventManager() {
        return eventManager;
    }

    @Override
    public @NotNull CommonTeamRegistry teamRegistry() {
        return teamRegistry;
    }

    public @NotNull String databaseNameSuffixMaps() {
        return databaseNameSuffixMaps;
    }

    @Override
    public @NotNull CommonLobbyData lobbyData() {
        return lobbyData;
    }

    @Override
    public abstract @NotNull CommonWoolBattleCommands commands();

    @Override
    public abstract @Nullable CommonWBUser user(User user);

    public abstract @NotNull CommonWoolBattle woolbattle();

    public abstract @NotNull GamePhaseCreator gamePhaseCreator();

    public abstract @NotNull CompletableFuture<Void> fullyLoadedFuture();

    public abstract @NotNull PlatformWorldHandler worldHandler();
}
