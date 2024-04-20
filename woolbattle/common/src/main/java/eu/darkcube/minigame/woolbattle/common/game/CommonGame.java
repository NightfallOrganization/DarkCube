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
import eu.darkcube.minigame.woolbattle.api.event.game.UserJoinGameEvent;
import eu.darkcube.minigame.woolbattle.api.event.game.UserQuitGameEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.SetupUserGameEvent;
import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.event.world.WorldEvent;
import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.api.world.GameWorld;
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
    private final @NotNull CommonLobbyData lobbyData;
    private final @NotNull CommonIngameData ingameData = new CommonIngameData();
    private final @NotNull MapSize mapSize;
    private final @NotNull CommonTeamManager teamManager;
    private volatile CommonPhase phase;
    private volatile @NotNull CommonMap map;

    CommonGame(@NotNull CommonWoolBattleApi woolbattle, @NotNull UUID id, @NotNull Map map) {
        this.woolbattle = woolbattle;
        this.id = id;
        this.eventManager = EventNode.type("game-" + id, EventFilter.ALL, (event, unused) -> {
            if (event instanceof GameEvent gameEvent) {
                var game = gameEvent.game();
                return game == this;
            }
            if (event instanceof UserEvent userEvent) {
                var user = userEvent.user();
                var game = user.game();
                return game == this;
            }
            if (event instanceof WorldEvent worldEvent) {
                var world = worldEvent.world();
                if (world instanceof GameWorld gameWorld) {
                    var game = gameWorld.game();
                    return game == this;
                }
            }
            return true;
        });
        this.map = (CommonMap) map;
        this.mapSize = map.size();
        this.teamManager = new CommonTeamManager(this, mapSize);
        this.lobbyData = woolbattle.lobbyData().clone();
    }

    void init() {
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
    public void map(@NotNull Map map) {
        this.map = (CommonMap) map;
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
        var event = new UserJoinGameEvent(user, this, UserJoinGameEvent.Result.CANNOT_JOIN, null);
        woolbattle.eventManager().call(event);
        var result = event.result();
        var location = event.spawnLocation();
        switch (result) {
            case USER_PLAYING -> addToPlaying(user);
            case USER_SPECTATING -> addToSpectating(user);
            case CANNOT_JOIN -> {
                return new JoinResult(null);
            }
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
        woolbattle.lobbySystemLink().update();
        return new JoinResult(location);
    }

    public void playerQuit(@NotNull CommonWBUser user) {
        var event = new UserQuitGameEvent(user, this);
        woolbattle.eventManager().call(event);
        users.remove(user);
        removeFromPlaying(user);
        removeFromSpectating(user);
        checkUnload();
        woolbattle.lobbySystemLink().update();
    }

    public void playerSetup(@NotNull CommonWBUser user) {
        var event = new SetupUserGameEvent(user, this);
        woolbattle.eventManager().call(event);
    }

    public void addToSpectating(@NotNull CommonWBUser user) {
        spectatingPlayers.add(user);
    }

    public void removeFromSpectating(@NotNull CommonWBUser user) {
        spectatingPlayers.remove(user);
    }

    public void addToPlaying(@NotNull CommonWBUser user) {
        playingPlayers.add(user);
    }

    public void removeFromPlaying(@NotNull CommonWBUser user) {
        playingPlayers.remove(user);
    }

    /**
     * @param location null to deny, else the spawn location
     */
    public record JoinResult(@Nullable Location location) {
    }
}
