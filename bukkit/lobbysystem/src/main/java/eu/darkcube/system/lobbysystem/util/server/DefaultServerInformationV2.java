/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.server;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.util.GameState;

public class DefaultServerInformationV2 extends DefaultServerInformation {
    private final String key;

    public DefaultServerInformationV2(Lobby lobby, String key) {
        super(lobby);
        this.key = key;
    }

    @Override
    public CompletableFuture<State> connectPlayer(UUID uuid) {
        return lobby.serverManager().startConnectionRequest(uuid, this);
    }

    @Override
    public int maxPlayers() {
        return entry().readProperty(DarkCubeServiceProperty.MAX_PLAYING_PLAYERS);
    }

    @Override
    public int onlinePlayers() {
        return entry().readProperty(DarkCubeServiceProperty.PLAYING_PLAYERS);
    }

    @Override
    public @Nullable GameState gameState() {
        return entry().readProperty(DarkCubeServiceProperty.GAME_STATE);
    }

    @Override
    public int spectators() {
        return entry().readProperty(DarkCubeServiceProperty.SPECTATING_PLAYERS);
    }

    @Override
    public String displayNameString() {
        return entry().readProperty(DarkCubeServiceProperty.DISPLAY_NAME);
    }

    @Override
    public Component displayName() {
        var displayName = displayNameString();
        if (displayName == null) return null;
        return GsonComponentSerializer.gson().deserialize(displayName);
    }

    @Override
    public boolean online() {
        return true;
    }

    @Override
    public boolean autoconfigured() {
        return false;
    }

    public Document document() {
        return entry().readDocument("document");
    }

    private Document entry() {
        return snapshot.readProperty(DarkCubeServiceProperty.EXTRA).readDocument("LobbyV2").readDocument(key);
    }

    public String key() {
        return key;
    }
}
