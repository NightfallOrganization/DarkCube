/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.game;

import java.util.Collection;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.api.WoolBattleApi;
import eu.darkcube.minigame.woolbattle.api.game.ingame.IngameData;
import eu.darkcube.minigame.woolbattle.api.game.lobby.LobbyData;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.api.team.TeamManager;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.SchedulerManager;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.GameState;

@Api
public interface Game {
    @Api
    @NotNull
    WoolBattleApi api();

    @Api
    @NotNull
    MapSize mapSize();

    @Api
    @NotNull
    Map map();

    @Api
    void map(@NotNull Map map);

    @Api
    @NotNull
    UUID id();

    @Api
    @NotNull
    GamePhase phase();

    @Api
    @UnmodifiableView
    @NotNull
    Collection<@NotNull ? extends WBUser> users();

    @Api
    @UnmodifiableView
    @NotNull
    Collection<@NotNull ? extends WBUser> playingPlayers();

    @Api
    @UnmodifiableView
    @NotNull
    Collection<@NotNull ? extends WBUser> spectatingPlayers();

    @Api
    @NotNull
    PerkRegistry perkRegistry();

    @Api
    @NotNull
    SchedulerManager scheduler();

    @Api
    @NotNull
    EventNode<Object> eventManager();

    @Api
    @NotNull
    GameState gameState();

    @Api
    @NotNull
    LobbyData lobbyData();

    @Api
    @NotNull
    TeamManager teamManager();

    @Api
    @NotNull
    IngameData ingameData();
}
