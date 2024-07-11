/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common;

import static eu.darkcube.system.cloudnet.DarkCubeServiceProperty.*;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.space;
import static eu.darkcube.system.libs.net.kyori.adventure.text.Component.text;
import static eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor.GRAY;
import static eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor.LIGHT_PURPLE;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.channel.ChannelMessageSender;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.document.property.DocProperty;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.minigame.woolbattle.api.LobbySystemLink;
import eu.darkcube.minigame.woolbattle.api.game.ingame.Ingame;
import eu.darkcube.minigame.woolbattle.api.game.lobby.Lobby;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.api.util.scheduler.TaskSchedule;
import eu.darkcube.minigame.woolbattle.common.game.CommonGame;
import eu.darkcube.minigame.woolbattle.common.map.CommonMap;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.server.cloudnet.DarkCubeServerCloudNet;
import eu.darkcube.system.util.GameState;

public class CommonLobbySystemLink implements LobbySystemLink {
    private static final DocProperty<UUID> GAME_ID = DocProperty.property("gameId", UUID.class);
    private static final DocProperty<MapSize> MAP_SIZE = DocProperty.property("mapSize", MapSize.class).withDefault(null);
    private static final DocProperty<String> MAP_NAME = DocProperty.property("mapName", String.class).withDefault(null);
    private static final DocProperty<Boolean> ADMIN_SETUP = DocProperty.property("adminSetup", Boolean.class).withDefault(false);
    private static final Logger LOGGER = Logger.getLogger("CommonLobbySystemLink");
    private final CommonWoolBattleApi woolbattle;
    private final Cache<UUID, ConnectionRequest> connectionRequests;
    private final RequestListener requestListener;
    private final boolean pServer = false;
    private final AtomicBoolean enabled = new AtomicBoolean(false);
    private volatile boolean fullyLoaded = false;

    public CommonLobbySystemLink(CommonWoolBattleApi woolbattle) {
        this.woolbattle = woolbattle;
        this.connectionRequests = Caffeine.newBuilder().scheduler(Scheduler.systemScheduler()).expireAfterWrite(Duration.ofSeconds(10)).removalListener((UUID key, ConnectionRequest value, RemovalCause cause) -> {
            if (cause.wasEvicted()) { // Connection timed out
                if (value != null) {
                    var game = value.game;
                    if (game != null) {
                        game.checkUnload();
                    }
                } else {
                    LOGGER.severe("ConnectionRequest was null when it shouldn't be possible");
                }
            }
        }).build();
        this.requestListener = new RequestListener();
    }

    public void enable() {
        if (!enabled.compareAndSet(false, true)) return;
        DarkCubeServerCloudNet.autoConfigure(false);
        InjectionLayer.boot().instance(EventManager.class).registerListener(requestListener);
        woolbattle.fullyLoadedFuture().thenRun(() -> {
            this.fullyLoaded = true;
            update();
        });
    }

    public void disable() {
        if (!enabled.compareAndSet(true, false)) return;
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(requestListener);
    }

    private void sendIsLoaded() {
        ChannelMessage.builder().channel("darkcube_lobbysystem").message("pserver_loaded").targetServices().buffer(DataBuf.empty()).build().send();
    }

    @Override
    public boolean enabled() {
        return enabled.get();
    }

    public Cache<UUID, ConnectionRequest> connectionRequests() {
        return connectionRequests;
    }

    @Override
    public void update() {
        if (!enabled()) return;
        if (!fullyLoaded) return;
        DarkCubeServerCloudNet.gameState(GameState.LOBBY);
        DarkCubeServerCloudNet.displayName("LobbyV2");
        DarkCubeServerCloudNet.extra(root -> {
            var doc = Document.newJsonDocument();
            for (var game : woolbattle.games().games()) {
                var key = game.id().toString();
                doc.append(key, createEntry(game));
            }
            var maps = new HashMap<MapSize, CommonMap>();
            for (var map : woolbattle.mapManager().maps().stream().filter(Map::enabled).toList()) {
                maps.putIfAbsent(map.size(), map);
            }
            for (var entry : maps.entrySet()) {
                var mapSize = entry.getKey();
                var map = entry.getValue();
                doc.append(mapSize.toString(), createEntry(map));
            }
            if (doc.empty()) {
                // WoolBattle is not correctly set up. Create a dummy world to allow admins to join
                var entry = Document.newJsonDocument();
                var protocol = Document.newJsonDocument();
                protocol.writeProperty(ADMIN_SETUP, true);

                entry.writeProperty(DISPLAY_NAME, GsonComponentSerializer.gson().serialize(Component.text("Setup WoolBattle")));
                entry.writeProperty(PLAYING_PLAYERS, 0);
                entry.writeProperty(MAX_PLAYING_PLAYERS, 1);
                entry.writeProperty(SPECTATING_PLAYERS, 0);
                entry.writeProperty(GAME_STATE, GameState.LOBBY);
                entry.append("document", protocol);
                doc.append("setup", entry);
            }
            root.append("LobbyV2", doc);
        });
        var serviceInfoHolder = InjectionLayer.boot().instance(ServiceInfoHolder.class);
        serviceInfoHolder.publishServiceInfoUpdate();
    }

    private Document createEntry(CommonMap map) {
        var entry = Document.newJsonDocument();

        var protocol = Document.newJsonDocument();
        protocol.writeProperty(MAP_SIZE, map.size());
        protocol.writeProperty(MAP_NAME, map.name());

        entry.writeProperty(DISPLAY_NAME, displayName(map));
        entry.writeProperty(PLAYING_PLAYERS, 0);
        entry.writeProperty(MAX_PLAYING_PLAYERS, map.size().teams() * map.size().teamSize());
        entry.writeProperty(SPECTATING_PLAYERS, 0);
        entry.writeProperty(GAME_STATE, GameState.LOBBY);
        entry.append("document", protocol);
        return entry;
    }

    private Document createEntry(CommonGame game) {
        var entry = Document.newJsonDocument();

        var protocol = Document.newJsonDocument();
        protocol.writeProperty(MAP_SIZE, game.mapSize());
        protocol.writeProperty(GAME_ID, game.id());

        entry.writeProperty(DISPLAY_NAME, displayName(game));
        entry.writeProperty(PLAYING_PLAYERS, game.playingPlayers().size());
        entry.writeProperty(MAX_PLAYING_PLAYERS, game.mapSize().teams() * game.mapSize().teamSize());
        entry.writeProperty(SPECTATING_PLAYERS, game.spectatingPlayers().size());
        entry.writeProperty(GAME_STATE, game.gameState());
        entry.append("document", protocol);
        return entry;
    }

    private String displayName(CommonMap map) {
        var component = text(map.name(), LIGHT_PURPLE).append(space()).append(text("(" + map.size() + ")", GRAY));
        return GsonComponentSerializer.gson().serialize(component);
    }

    private String displayName(CommonGame game) {
        var map = game.map();
        return displayName(map);
    }

    public record ConnectionRequest(@NotNull UUID player, @Nullable CommonGame game) {
    }

    private class RequestListener {
        private void responseV2(@NotNull ChannelMessageSender target, @NotNull UUID requestId, int status, @Nullable String message) {
            var buffer = DataBuf.empty().writeUniqueId(requestId).writeInt(status);
            if (message != null) buffer.writeString(message);
            ChannelMessage.builder().target(target.toTarget()).channel("darkcube_lobbysystem_v2").message("connection_request_status").buffer(buffer).build().send();
        }

        @EventListener
        public void handle(ChannelMessageReceiveEvent event) {
            if (event.channel().equals("darkcube_lobbysystem_v2")) handleV2(event);
            else if (event.channel().equals("darkcube_lobbysystem")) handle_(event);
        }

        private void handle_(ChannelMessageReceiveEvent event) {
            var msg = event.message();
            if (msg.equals("send_is_loaded")) {
                if (!enabled()) return;
                if (!pServer) return;
                var self = PServerProvider.instance().currentPServer();
                var id = event.content().readString();
                if (!id.equals(self.id().toString())) return;
                sendIsLoaded();
            }
        }

        private void handleV2(ChannelMessageReceiveEvent event) {
            var msg = event.message();
            @NotNull var sender = event.sender();
            if (msg.equals("start_connection_request")) {
                @NotNull var buffer = event.content();
                @NotNull var requestId = buffer.readUniqueId();
                @NotNull var playerUniqueId = buffer.readUniqueId();
                @NotNull var protocolDocument = buffer.readObject(Document.class);
                @Nullable var mapSize = protocolDocument.readProperty(MAP_SIZE);
                @Nullable var mapName = protocolDocument.readProperty(MAP_NAME);
                @Nullable var gameId = protocolDocument.readProperty(GAME_ID);
                @NotNull var adminSetup = protocolDocument.readProperty(ADMIN_SETUP);
                if (adminSetup) {
                    var connectionRequest = new ConnectionRequest(playerUniqueId, null);
                    connectionRequests.put(requestId, connectionRequest);
                    responseV2(sender, requestId, 2, null);
                    return;
                }
                if (mapSize == null) {
                    responseV2(sender, requestId, 0, "invalid_protocol");
                    return;
                }
                if (gameId != null) {
                    @Nullable var game = woolbattle.games().game(gameId);
                    if (game == null) {
                        responseV2(sender, requestId, 0, "out_of_date");
                        return;
                    }
                    connect(game, playerUniqueId, requestId, sender, mapSize);
                } else if (mapName != null) {
                    @Nullable var map = woolbattle.mapManager().map(mapName, mapSize);
                    if (map == null) {
                        responseV2(sender, requestId, 0, "out_of_date");
                        return;
                    }
                    @NotNull var game = woolbattle.games().createGame(map);
                    connect(game, playerUniqueId, requestId, sender, mapSize);
                } else {
                    responseV2(sender, requestId, 0, "invalid_protocol");
                }
            }
        }

        private void connect(@NotNull CommonGame game, @NotNull UUID playerUniqueId, @NotNull UUID requestId, @NotNull ChannelMessageSender sender, @NotNull MapSize mapSize) {
            var connectionRequest = new ConnectionRequest(playerUniqueId, game);
            connectionRequests.put(requestId, connectionRequest);
            responseV2(sender, requestId, 1, null);
            LOGGER.info("Connecting " + playerUniqueId + " to game " + game.id() + " with requestId " + requestId);
            game.scheduler().schedule(() -> {
                var phase = game.phase();
                if (phase instanceof Ingame) {
                    responseV2(sender, requestId, 2, null);
                    return;
                }
                if (phase instanceof Lobby) {
                    @Nullable var loadedMapSize = game.mapSize();
                    if (!loadedMapSize.equals(mapSize)) {
                        responseV2(sender, requestId, 0, "out_of_date");
                        return;
                    }
                    responseV2(sender, requestId, 2, null);
                    return;
                }
                responseV2(sender, requestId, 0, "out_of_date");
            }, TaskSchedule.immediate(), TaskSchedule.stop());
        }
    }
}
