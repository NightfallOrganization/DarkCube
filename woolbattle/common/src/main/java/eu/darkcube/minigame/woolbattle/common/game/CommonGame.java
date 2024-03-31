/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.game;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import eu.darkcube.minigame.woolbattle.api.event.game.GameEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngameData;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobbyData;
import eu.darkcube.minigame.woolbattle.common.map.CommonMap;
import eu.darkcube.minigame.woolbattle.common.team.CommonTeamManager;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.CommonSchedulerManager;
import eu.darkcube.system.event.EventFilter;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.GameState;

public class CommonGame implements Game {
    private final @NotNull CommonWoolBattleApi woolbattle;
    private final @NotNull UUID id;
    private final @NotNull Collection<CommonWBUser> users = new CopyOnWriteArraySet<>();
    private final @NotNull Collection<CommonWBUser> playingPlayers = new CopyOnWriteArraySet<>();
    private final @NotNull Collection<CommonWBUser> spectatingPlayers = new CopyOnWriteArraySet<>();
    private final @NotNull PerkRegistry perkRegistry = new PerkRegistry();
    private final @NotNull CommonSchedulerManager scheduler = new CommonSchedulerManager();
    private final @NotNull EventNode<Object> eventManager;
    private final @NotNull CommonLobbyData lobbyData = new CommonLobbyData();
    private final @NotNull CommonIngameData ingameData = new CommonIngameData();
    private final @NotNull MapSize mapSize;
    private final @NotNull CommonTeamManager teamManager;
    private volatile CommonPhase phase;
    private volatile CommonMap map;

    public CommonGame(@NotNull CommonWoolBattleApi woolbattle, @NotNull UUID id, @NotNull MapSize mapSize) {
        this.woolbattle = woolbattle;
        this.id = id;
        this.eventManager = EventNode.value("game-" + id, EventFilter.ALL, event -> {
            if (event instanceof GameEvent gameEvent) {
                return gameEvent.game() == this;
            }
            if (event instanceof UserEvent userEvent) {
                var user = userEvent.user();
                var game = user.game();
                return game == this;
            }
            return true;
        });
        this.mapSize = mapSize;
        this.teamManager = new CommonTeamManager(this, mapSize);
        this.lobbyData.load(this);
        this.phase = woolbattle.gamePhaseCreator().createLobby(this);
        this.phase.enable();
    }

    @Override
    public @NotNull CommonWoolBattleApi woolbattle() {
        return woolbattle;
    }

    @Override
    public @NotNull MapSize mapSize() {
        return mapSize;
    }

    @Override
    public @NotNull CommonMap map() {
        return map;
    }

    @Override
    public @NotNull UUID id() {
        return id;
    }

    @Override
    public @NotNull CommonPhase phase() {
        return phase;
    }

    public void enableNextPhase() {
        phase.disable();
        if (phase instanceof CommonLobby) {
            phase = woolbattle.gamePhaseCreator().createIngame(this);
        } else if (phase instanceof CommonIngame) {
            phase = woolbattle.gamePhaseCreator().createEndgame(this);
        } else {
            throw new IllegalStateException("Invalid phase: " + phase);
        }
        phase.enable();
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull CommonWBUser> users() {
        return Collections.unmodifiableCollection(users);
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull CommonWBUser> playingPlayers() {
        return Collections.unmodifiableCollection(playingPlayers);
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull CommonWBUser> spectatingPlayers() {
        return Collections.unmodifiableCollection(spectatingPlayers);
    }

    @Override
    public @NotNull PerkRegistry perkRegistry() {
        return perkRegistry;
    }

    @Override
    public @NotNull CommonSchedulerManager scheduler() {
        return scheduler;
    }

    @Override
    public @NotNull EventNode<Object> eventManager() {
        return eventManager;
    }

    @Override
    public @NotNull GameState gameState() {
        return phase().gameState();
    }

    @Override
    public @NotNull CommonLobbyData lobbyData() {
        return lobbyData;
    }

    @Override
    public @NotNull CommonTeamManager teamManager() {
        return teamManager;
    }

    @Override
    public @NotNull CommonIngameData ingameData() {
        return ingameData;
    }

    public void checkUnload() {
        if (!users.isEmpty()) {
            return;
        }
        // no users in game, unload it
        woolbattle.games().unload(this);
    }

    void unload0() {
        phase.disable();
        phase = null;
    }

    public @NotNull JoinResult playerJoined(@NotNull CommonWBUser user) {
        users.add(user);
        if (phase instanceof CommonLobby lobby) {
            playingPlayers.add(user);
            var spawn = lobby.spawn();
            return new JoinResult(spawn);
        }
        return new JoinResult(null);
    }

    public void playerQuit(@NotNull CommonWBUser user) {
        users.remove(user);
        playingPlayers.remove(user);
        spectatingPlayers.remove(user);
        checkUnload();
    }

    /**
     * @param location null to deny, else the spawn location
     */
    public record JoinResult(@Nullable Location location) {
    }
}
