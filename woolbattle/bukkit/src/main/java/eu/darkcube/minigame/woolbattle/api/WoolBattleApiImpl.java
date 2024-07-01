/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.api.command.WoolBattleCommands;
import eu.darkcube.minigame.woolbattle.api.command.arguments.WoolBattleArguments;
import eu.darkcube.minigame.woolbattle.api.entity.EntityImplementations;
import eu.darkcube.minigame.woolbattle.api.game.GameManager;
import eu.darkcube.minigame.woolbattle.api.game.lobby.LobbyData;
import eu.darkcube.minigame.woolbattle.api.map.MapManager;
import eu.darkcube.minigame.woolbattle.api.team.TeamRegistry;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.MaterialProvider;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerManager;
import eu.darkcube.minigame.woolbattle.api.world.ColoredWoolProvider;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.PersistentDataStorage;

public class WoolBattleApiImpl implements WoolBattleApi {
    private final WoolBattleBukkit woolbattle;

    public WoolBattleApiImpl(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override
    public @NotNull GameManager games() {
        return null;
    }

    @Override
    public @NotNull LobbySystemLinkImpl lobbySystemLink() {
        return woolbattle.lobbySystemLink();
    }

    @Override
    public @NotNull EntityImplementations entityImplementations() {
        return null;
    }

    @Override
    public @NotNull MaterialProvider materialProvider() {
        return null;
    }

    @Override
    public @NotNull MapManager mapManager() {
        return null;
    }

    @Override
    public @NotNull SchedulerManager scheduler() {
        return null;
    }

    @Override
    public @NotNull PersistentDataStorage persistentDataStorage() {
        return null;
    }

    @Override
    public @NotNull WoolBattleArguments commandArguments() {
        return null;
    }

    @Override
    public @NotNull WoolBattleCommands commands() {
        return null;
    }

    @Override
    public @NotNull TeamRegistry teamRegistry() {
        return null;
    }

    @Override
    public @NotNull ColoredWoolProvider woolProvider() {
        return null;
    }

    @Override
    public @NotNull EventNode<Object> eventManager() {
        return null;
    }

    @Override
    public @NotNull LobbyData lobbyData() {
        return null;
    }

    @Override
    public @Nullable WBUser user(User user) {
        return null;
    }

    @Override
    public @NotNull String namespace() {
        return "woolbattle";
    }
}
