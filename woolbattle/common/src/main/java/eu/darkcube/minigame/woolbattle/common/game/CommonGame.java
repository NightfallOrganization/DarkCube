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

import eu.darkcube.minigame.woolbattle.api.game.Game;
import eu.darkcube.minigame.woolbattle.api.game.ingame.IngameData;
import eu.darkcube.minigame.woolbattle.api.game.lobby.LobbyData;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngame;
import eu.darkcube.minigame.woolbattle.common.game.ingame.CommonIngameData;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobby;
import eu.darkcube.minigame.woolbattle.common.game.lobby.CommonLobbyData;
import eu.darkcube.minigame.woolbattle.common.map.CommonMap;
import eu.darkcube.minigame.woolbattle.common.util.scheduler.CommonSchedulerManager;
import eu.darkcube.system.event.EventNode;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.GameState;

public class CommonGame implements Game {
    private final CommonWoolBattleApi woolbattle;
    private final UUID id;
    private final Collection<WBUser> users = new CopyOnWriteArraySet<>();
    private final Collection<WBUser> playingPlayers = new CopyOnWriteArraySet<>();
    private final Collection<WBUser> spectatingPlayers = new CopyOnWriteArraySet<>();
    private final PerkRegistry perkRegistry = new PerkRegistry();
    private final CommonSchedulerManager scheduler = new CommonSchedulerManager();
    private final EventNode<Object> eventManager;
    private final CommonLobbyData lobbyData = new CommonLobbyData();
    private final CommonIngameData ingameData = new CommonIngameData();
    private volatile CommonPhase phase;
    private volatile CommonMap map;

    public CommonGame(CommonWoolBattleApi woolbattle, UUID id) {
        this.woolbattle = woolbattle;
        this.id = id;
        this.eventManager = EventNode.all("game-" + id);
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
        return map().size();
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
    public @UnmodifiableView @NotNull Collection<WBUser> users() {
        return Collections.unmodifiableCollection(users);
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull WBUser> playingPlayers() {
        return Collections.unmodifiableCollection(playingPlayers);
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull WBUser> spectatingPlayers() {
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
    public @NotNull LobbyData lobbyData() {
        return null;
    }

    @Override
    public @NotNull IngameData ingameData() {
        return null;
    }

    public void checkUnload() {
        // TODO
    }
}
