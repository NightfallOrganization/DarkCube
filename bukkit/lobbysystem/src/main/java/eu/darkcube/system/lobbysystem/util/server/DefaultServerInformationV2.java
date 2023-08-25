/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.server;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.lobbysystem.Lobby;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DefaultServerInformationV2 extends DefaultServerInformation {
    private final String key;

    public DefaultServerInformationV2(Lobby lobby, String key) {
        super(lobby);
        this.key = key;
    }

    @Override public CompletableFuture<State> connectPlayer(UUID uuid) {
        return lobby.serverManager().startConnectionRequest(uuid, this);
    }

    @Override public int maxPlayers() {
        return entry().getInt("maxPlayers");
    }

    @Override public int onlinePlayers() {
        return entry().getInt("onlinePlayers");
    }

    @Override public Component displayName() {
        return GsonComponentSerializer.gson().deserialize(entry().getString("displayName"));
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
