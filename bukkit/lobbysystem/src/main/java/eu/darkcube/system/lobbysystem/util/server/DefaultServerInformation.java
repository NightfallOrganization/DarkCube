/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.server;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.util.GameState;

public class DefaultServerInformation implements ServerInformation {

    protected final Lobby lobby;
    protected final UUID uniqueId = UUID.randomUUID();
    protected volatile ServiceInfoSnapshot snapshot;

    public DefaultServerInformation(Lobby lobby) {
        this.lobby = lobby;
    }

    public DefaultServerInformation snapshot(ServiceInfoSnapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public Lobby lobby() {
        return lobby;
    }

    @Override
    public @Nullable Component displayName() {
        var displayName = displayNameString();
        if (displayName == null) return null;
        return LegacyComponentSerializer.legacySection().deserialize(displayName);
    }

    public String displayNameString() {
        return snapshot.readProperty(DarkCubeServiceProperty.DISPLAY_NAME);
    }

    public boolean autoconfigured() {
        return snapshot.readProperty(DarkCubeServiceProperty.AUTOCONFIGURED);
    }

    @Override
    public String taskName() {
        return snapshot.serviceId().taskName();
    }

    @Override
    public boolean online() {
        var gameState = gameState();
        if (gameState == null) return false;
        var autoconfigured = autoconfigured();
        if (gameState != GameState.INGAME && autoconfigured) return false;
        var displayName = displayName();
        return displayName != null;
    }

    @Override
    public UUID uniqueId() {
        return uniqueId;
    }

    @Override
    public int onlinePlayers() {
        return snapshot.readProperty(DarkCubeServiceProperty.PLAYING_PLAYERS);
    }

    @Override
    public int maxPlayers() {
        return snapshot.readProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS);
    }

    @Override
    public int spectators() {
        return snapshot.readProperty(DarkCubeServiceProperty.SPECTATING_PLAYERS);
    }

    @Override
    public @Nullable GameState gameState() {
        return snapshot.readProperty(DarkCubeServiceProperty.GAME_STATE);
    }

    @Override
    public CompletableFuture<State> connectPlayer(UUID uuid) {
        lobby.playerManager().playerExecutor(uuid).connect(snapshot.name());
        return CompletableFuture.completedFuture(new State(true, null));
    }
}
