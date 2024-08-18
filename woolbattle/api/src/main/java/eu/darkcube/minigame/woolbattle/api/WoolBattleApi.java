/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import java.time.temporal.TemporalUnit;
import java.util.UUID;

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
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.net.kyori.adventure.key.Namespaced;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.PersistentDataStorage;

@Api
public interface WoolBattleApi extends Namespaced {
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

    @Api
    @NotNull
    WoolBattleArguments commandArguments();

    @Api
    @NotNull
    WoolBattleCommands commands();

    @Api
    @NotNull
    TeamRegistry teamRegistry();

    @Api
    @NotNull
    ColoredWoolProvider woolProvider();

    @Api
    @NotNull
    EventNode<Object> eventManager();

    @Api
    @NotNull
    LobbyData lobbyData();

    @Api
    @NotNull
    TemporalUnit tickUnit();

    /**
     * Get a user by the UserAPI {@link User}. May be null in case no {@link WBUser} was found
     *
     * @param user the {@link UserAPI} type
     * @return the {@link WBUser}, null if not found
     */
    @Api
    @Nullable
    WBUser user(User user);

    @Api
    @Nullable
    WBUser user(String playerName);

    @Api
    @Nullable
    WBUser user(UUID uniqueId);
}
