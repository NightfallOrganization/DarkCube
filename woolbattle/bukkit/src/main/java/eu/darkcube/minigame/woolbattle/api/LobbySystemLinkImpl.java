/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

import java.time.Duration;
import java.util.UUID;
import java.util.stream.Collectors;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.channel.ChannelMessageSender;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.modules.bridge.BridgeServiceHelper;
import eu.cloudnetservice.wrapper.holder.ServiceInfoHolder;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.server.cloudnet.DarkCubeServerCloudNet;
import eu.darkcube.system.util.GameState;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;

public class LobbySystemLinkImpl implements LobbySystemLink {

    private final WoolBattleBukkit woolbattle;
    private final UpdateTask updateTask;
    private final RequestListener requestListener = new RequestListener();
    private final Cache<UUID, ConnectionRequest> connectionRequests;
    private final boolean pserver = PServerProvider.instance().isPServer();
    private boolean fullyLoaded = false;
    private volatile boolean enabled = false;

    public LobbySystemLinkImpl(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.updateTask = new UpdateTask(woolbattle);
        this.connectionRequests = Caffeine.newBuilder().scheduler(com.github.benmanes.caffeine.cache.Scheduler.systemScheduler()).expireAfterWrite(Duration.ofSeconds(10)).removalListener(newRemovalListener()).build();
    }

    private RemovalListener<UUID, ConnectionRequest> newRemovalListener() {
        return (ignoredUUID, value, cause) -> {
            if (cause.wasEvicted()) { // Connection timeout
                schedule(() -> {
                    woolbattle.lobby().checkUnload();
                });
            }
        };
    }

    @Override
    public boolean enabled() {
        return enabled;
    }

    public void enable() {
        updateTask.runTaskTimer(50);
        fullyLoaded = false;
        InjectionLayer.boot().instance(EventManager.class).registerListener(requestListener);
        new Scheduler(woolbattle, this::setFullyLoaded).runTask();
        DarkCubeServerCloudNet.autoConfigure(false);
        if (pserver) {
            var executor = PServerProvider.instance().currentPServer();
            var doc = executor.storage().get(Key.key("lobbysystem", "pserver.gameserver.service"), PersistentDataTypes.JSON_OBJECT);
            if (doc != null) {
                var protocol = doc.get("protocol").getAsJsonObject();
                var data = protocol.get("data").getAsString();
                woolbattle.lobby().loadGame(MapSize.fromString(data));
                new Scheduler(woolbattle, this::sendIsLoaded).runTaskLater(5);
            }
        }
        enabled = true;
    }

    private void sendIsLoaded() {
        ChannelMessage.builder().channel("darkcube_lobbysystem").message("pserver_loaded").targetServices().buffer(DataBuf.empty()).build().send();
    }

    public void disable() {
        if (!enabled) return;
        InjectionLayer.boot().instance(EventManager.class).unregisterListener(requestListener);
        updateTask.cancel();
        enabled = false;
    }

    private void setFullyLoaded() {
        fullyLoaded = true;
        update();
    }

    public boolean pserver() {
        return pserver;
    }

    @Override
    public void update() {
        if (!fullyLoaded) return;
        if (!enabled) return;
        var gameState = gameState();
        DarkCubeServerCloudNet.gameState(gameState);
        DarkCubeServerCloudNet.playingPlayers().set(playingPlayers());
        DarkCubeServerCloudNet.maxPlayingPlayers().set(woolbattle.maxPlayers());
        var serviceInfoHolder = InjectionLayer.boot().instance(ServiceInfoHolder.class);
        var serviceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);
        serviceHelper.maxPlayers().set(1000);
        DarkCubeServerCloudNet.displayName("LobbyV2");
        DarkCubeServerCloudNet.extra(root -> {
            var doc = Document.newJsonDocument();
            var gameData = woolbattle.gameData();
            @Nullable MapSize gameDataSize = gameData.mapSize();
            if (gameState == GameState.LOBBY) {
                if (gameDataSize == null) {
                    var knownByMaps = woolbattle.mapManager().getMaps().stream().map(Map::size).collect(Collectors.toSet());
                    for (var mapSize : knownByMaps) {
                        write(doc, mapSize);
                    }
                } else {
                    @Nullable Map map = gameData.map();
                    if (map == null) {
                        write(doc, gameDataSize);
                    } else {
                        write(doc, map);
                    }
                }
            } else if (gameState == GameState.INGAME) {
                var map = woolbattle.gameData().map();
                if (map != null) {
                    write(doc, map);
                }
            }

            root.append("LobbyV2", doc);
        });
        serviceInfoHolder.publishServiceInfoUpdate();
    }

    public Cache<UUID, ConnectionRequest> connectionRequests() {
        return connectionRequests;
    }

    private void write(@NotNull Document.Mutable doc, @NotNull MapSize mapSize) {
        var map = woolbattle.mapManager().defaultRandomPersistentMap(mapSize);
        if (map == null) return;
        write(doc, map);
    }

    private void write(@NotNull Document.Mutable doc, @NotNull Map map) {
        doc.append(map.size().toString(), createEntry(map));
    }

    @NotNull
    private Document createEntry(@NotNull Map map) {
        var mapSize = map.size();
        var protocol = Document.newJsonDocument();
        protocol.append("mapSize", mapSize);

        var onlinePlayers = 0;
        var gameData = woolbattle.gameData();
        if (gameData != null && gameData.mapSize() != null && gameData.mapSize().equals(mapSize)) {
            if (!woolbattle.lobby().enabled()) {
                onlinePlayers = (int) WBUser.onlineUsers().stream().filter(u -> u.getTeam().canPlay()).count();
            } else {
                onlinePlayers = Bukkit.getOnlinePlayers().size();
            }
        }

        var entry = Document.newJsonDocument();
        entry.append("displayName", GsonComponentSerializer.gson().serialize(displayName(map)));
        entry.append("onlinePlayers", onlinePlayers);
        entry.append("maxPlayers", mapSize.teams() * mapSize.teamSize());
        entry.append("document", protocol);
        return entry;
    }

    @NotNull
    private Component displayName(@NotNull Map map) {
        return Component.text(map.getName(), NamedTextColor.LIGHT_PURPLE).append(Component.space()).append(Component.text("(" + map.size() + ")", NamedTextColor.GRAY));
    }

    private int playingPlayers() {
        if (woolbattle.lobby().enabled()) return WBUser.onlineUsers().size();
        if (woolbattle.ingame().enabled()) return (int) WBUser.onlineUsers().stream().filter(u -> !u.getTeam().isSpectator()).count();
        return 0;
    }

    private GameState gameState() {
        if (woolbattle.lobby().enabled()) return GameState.LOBBY;
        if (woolbattle.ingame().enabled()) return GameState.INGAME;
        if (woolbattle.endgame().enabled()) return GameState.STOPPING;
        return GameState.UNKNOWN;
    }

    private void schedule(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) runnable.run();
        else new Scheduler(woolbattle, runnable).runTask();
    }

    public record ConnectionRequest(UUID player) {
    }

    private class RequestListener {

        private void responseV2(ChannelMessageSender target, UUID requestId, int status, @Nullable String message) {
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
                if (!enabled) return;
                if (!PServerProvider.instance().isPServer()) return;
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
                @Nullable var mapSize = protocolDocument.readObject("mapSize", MapSize.class);
                if (mapSize == null) {
                    responseV2(sender, requestId, 0, "invalid_protocol");
                    return;
                }
                var connectionRequest = new ConnectionRequest(playerUniqueId);
                connectionRequests.put(requestId, connectionRequest);
                responseV2(sender, requestId, 1, null);
                schedule(() -> {
                    if (!woolbattle.lobby().enabled()) {
                        if (woolbattle.ingame().enabled()) {
                            responseV2(sender, requestId, 2, null);
                            return;
                        }
                        responseV2(sender, requestId, 0, "out_of_date");
                        return;
                    }
                    @Nullable MapSize loadedMapSize = woolbattle.gameData().mapSize();
                    if (loadedMapSize != null) {
                        if (!loadedMapSize.equals(mapSize)) {
                            responseV2(sender, requestId, 0, "out_of_date");
                            return;
                        }
                        // Game is already loaded with the MapSize. Only joining is left to do
                    } else {
                        woolbattle.lobby().loadGame(mapSize);
                    }
                    responseV2(sender, requestId, 2, null);
                });
            }
        }
    }

    private class UpdateTask extends Scheduler {

        public UpdateTask(WoolBattleBukkit woolbattle) {
            super(woolbattle);
        }

        @Override
        public void run() {
            update();
        }
    }
}
