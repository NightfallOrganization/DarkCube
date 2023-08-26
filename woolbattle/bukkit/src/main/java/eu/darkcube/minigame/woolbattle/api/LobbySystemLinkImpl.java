/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api;

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
import eu.darkcube.minigame.woolbattle.GameData;
import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.map.Map;
import eu.darkcube.minigame.woolbattle.map.MapSize;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.DarkCubeBukkit;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.util.GameState;
import org.bukkit.Bukkit;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class LobbySystemLinkImpl implements LobbySystemLink {

    private final WoolBattleBukkit woolbattle;
    private final UpdateTask updateTask;
    private final RequestListener requestListener = new RequestListener();
    private final Cache<UUID, ConnectionRequest> connectionRequests;
    private boolean fullyLoaded = false;
    private boolean enabled = false;

    public LobbySystemLinkImpl(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
        this.updateTask = new UpdateTask(woolbattle);
        this.connectionRequests = Caffeine
                .newBuilder()
                .scheduler(com.github.benmanes.caffeine.cache.Scheduler.systemScheduler())
                .expireAfterWrite(Duration.ofSeconds(10))
                .removalListener(newRemovalListener())
                .build();
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

    @Override public boolean enabled() {
        return enabled;
    }

    public void enable() {
        if (PServerProvider.instance().isPServer()) return; // Do not enable if we are a PServer
        updateTask.runTaskTimer(50);
        fullyLoaded = false;
        InjectionLayer.boot().instance(EventManager.class).registerListener(requestListener);
        new Scheduler(woolbattle, this::setFullyLoaded).runTask();
        DarkCubeBukkit.autoConfigure(false);
        enabled = true;
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

    @Override public void update() {
        if (!fullyLoaded) return;
        if (!enabled) return;
        GameState gameState = gameState();
        DarkCubeBukkit.gameState(gameState);
        DarkCubeBukkit.playingPlayers().set(playingPlayers());
        DarkCubeBukkit.maxPlayingPlayers().set(woolbattle.maxPlayers());
        ServiceInfoHolder serviceInfoHolder = InjectionLayer.boot().instance(ServiceInfoHolder.class);
        BridgeServiceHelper serviceHelper = InjectionLayer.ext().instance(BridgeServiceHelper.class);
        serviceHelper.maxPlayers().set(1000);
        DarkCubeBukkit.displayName("LobbyV2");
        DarkCubeBukkit.extra(root -> {
            Document.Mutable doc = Document.newJsonDocument();
            GameData gameData = woolbattle.gameData();
            @Nullable MapSize gameDataSize = gameData.mapSize();
            if (gameState == GameState.LOBBY) {
                if (gameDataSize == null) {
                    Set<MapSize> knownByMaps = woolbattle.mapManager().getMaps().stream().map(Map::size).collect(Collectors.toSet());
                    for (MapSize mapSize : knownByMaps) {
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
            }

            root.append("LobbyV2", doc);
        });
        serviceInfoHolder.publishServiceInfoUpdate();
    }

    public Cache<UUID, ConnectionRequest> connectionRequests() {
        return connectionRequests;
    }

    private void write(@NotNull Document.Mutable doc, @NotNull MapSize mapSize) {
        Map map = woolbattle.mapManager().defaultRandomPersistentMap(mapSize);
        if (map == null) return;
        write(doc, map);
    }

    private void write(@NotNull Document.Mutable doc, @NotNull Map map) {
        doc.append(map.size().toString(), createEntry(map));
    }

    @NotNull private Document createEntry(@NotNull Map map) {
        MapSize mapSize = map.size();
        Document.Mutable protocol = Document.newJsonDocument();
        protocol.append("mapSize", mapSize);

        int onlinePlayers = 0;
        GameData gameData = woolbattle.gameData();
        if (gameData != null && gameData.mapSize() != null && gameData.mapSize().equals(mapSize)) {
            if (!woolbattle.lobby().enabled()) {
                onlinePlayers = (int) WBUser.onlineUsers().stream().filter(u -> u.getTeam().canPlay()).count();
            } else {
                onlinePlayers = Bukkit.getOnlinePlayers().size();
            }
        }

        Document.Mutable entry = Document.newJsonDocument();
        entry.append("displayName", GsonComponentSerializer.gson().serialize(displayName(map)));
        entry.append("onlinePlayers", onlinePlayers);
        entry.append("maxPlayers", mapSize.teams() * mapSize.teamSize());
        entry.append("document", protocol);
        return entry;
    }

    @NotNull private Component displayName(@NotNull Map map) {
        return Component
                .text(map.getName(), NamedTextColor.LIGHT_PURPLE)
                .append(Component.space())
                .append(Component.text("(" + map.size() + ")", NamedTextColor.GRAY));
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

        private void response(ChannelMessageSender target, UUID requestId, int status, @Nullable String message) {
            DataBuf.Mutable buffer = DataBuf.empty().writeUniqueId(requestId).writeInt(status);
            if (message != null) buffer.writeString(message);
            ChannelMessage
                    .builder()
                    .target(target.toTarget())
                    .channel("darkcube_lobbysystem_v2")
                    .message("connection_request_status")
                    .buffer(buffer)
                    .build()
                    .send();
        }

        @EventListener public void handle(ChannelMessageReceiveEvent event) {
            if (!event.channel().equals("darkcube_lobbysystem_v2")) return;
            @NotNull String msg = event.message();
            @NotNull ChannelMessageSender sender = event.sender();
            if (msg.equals("start_connection_request")) {
                @NotNull DataBuf buffer = event.content();
                @NotNull UUID requestId = buffer.readUniqueId();
                @NotNull UUID playerUniqueId = buffer.readUniqueId();
                @NotNull Document protocolDocument = buffer.readObject(Document.class);
                @Nullable MapSize mapSize = protocolDocument.readObject("mapSize", MapSize.class);
                if (mapSize == null) {
                    response(sender, requestId, 0, "invalid_protocol");
                    return;
                }
                ConnectionRequest connectionRequest = new ConnectionRequest(playerUniqueId);
                connectionRequests.put(requestId, connectionRequest);
                response(sender, requestId, 1, null);
                schedule(() -> {
                    if (!woolbattle.lobby().enabled()) {
                        response(sender, requestId, 0, "out_of_date");
                        return;
                    }
                    @Nullable MapSize loadedMapSize = woolbattle.gameData().mapSize();
                    if (loadedMapSize != null) {
                        if (!loadedMapSize.equals(mapSize)) {
                            response(sender, requestId, 0, "out_of_date");
                            return;
                        }
                        // Game is already loaded with the MapSize. Only joining is left to do
                    } else {
                        woolbattle.lobby().loadGame(mapSize);
                    }
                    response(sender, requestId, 2, null);
                });
            }
        }
    }

    private class UpdateTask extends Scheduler {

        public UpdateTask(WoolBattleBukkit woolbattle) {
            super(woolbattle);
        }

        @Override public void run() {
            update();
        }
    }
}
