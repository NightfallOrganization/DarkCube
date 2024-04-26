/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.server;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.Scheduler;
import eu.cloudnetservice.driver.channel.ChannelMessage;
import eu.cloudnetservice.driver.event.EventListener;
import eu.cloudnetservice.driver.event.EventManager;
import eu.cloudnetservice.driver.event.events.channel.ChannelMessageReceiveEvent;
import eu.cloudnetservice.driver.event.events.service.CloudServiceLifecycleChangeEvent;
import eu.cloudnetservice.driver.event.events.service.CloudServiceUpdateEvent;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.network.buffer.DataBuf;
import eu.cloudnetservice.driver.provider.CloudServiceProvider;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceLifeCycle;
import eu.darkcube.system.DarkCubeServiceProperty;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.pserver.common.PServerExecutor;
import org.bukkit.scheduler.BukkitRunnable;

public class ServerManager {

    private final Cache<UUID, ConnectionRequest> connectionRequests = Caffeine.newBuilder().scheduler(Scheduler.systemScheduler()).expireAfterWrite(Duration.ofSeconds(10)).removalListener((UUID ignoredUUID, ConnectionRequest value, RemovalCause cause) -> {
        if (cause.wasEvicted() && value != null) {
            value.future().completeExceptionally(new TimeoutException());
        }
    }).build();
    private final Lobby lobby;
    private final List<UpdateListener> updateListeners = new CopyOnWriteArrayList<>();
    private final Map<UUID, Collection<DefaultServerInformation>> informations = new HashMap<>();
    private final Map<UUID, DefaultServerInformation> mergedInformations = new HashMap<>();
    private final Map<UUID, ServiceInfoSnapshot> services = new HashMap<>();
    private final Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
    private volatile boolean requireUpdate = false;

    public ServerManager(Lobby lobby) {
        this.lobby = lobby;
        InjectionLayer.boot().instance(EventManager.class).registerListener(new Listener());
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!requireUpdate) return;
                requireUpdate = false;

                runQuery();
                for (var l : updateListeners) l.update(mergedInformations.values());
            }
        }.runTaskTimer(lobby, 1, 1);
    }

    public Collection<? extends ServerInformation> informations() {
        return Collections.unmodifiableCollection(mergedInformations.values());
    }

    public @NotNull CompletableFuture<@Nullable ServerInformation> forPServer(PServerExecutor pserver) {
        return pserver.serverName().thenCompose(serverName -> {
            var fut = new CompletableFuture<ServerInformation>();
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (var information : mergedInformations.values()) {
                        if (information.snapshot.name().equals(serverName)) {
                            fut.complete(information);
                            return;
                        }
                    }
                    fut.complete(null);
                }
            }.runTask(lobby);
            return fut;
        });
    }

    private void triggerUpdate() {
        requireUpdate = true;
    }

    private void runQuery() {
        Runnable r;
        while ((r = queue.poll()) != null) r.run();
        mergedInformations.clear();
        informations.keySet().retainAll(services.keySet());
        for (var entry : services.entrySet()) {
            var c = informations.compute(entry.getKey(), (ignoredUuid, prev) -> {
                if (prev != null) {
                    var v2 = false;
                    for (var information : prev) {
                        information.snapshot(entry.getValue());
                        if ("LobbyV2".equals(information.displayNameString()) || information instanceof DefaultServerInformationV2) {
                            v2 = true;
                            break;
                        }
                    }
                    if (v2) {
                        return parseV2(entry.getValue(), prev);
                    }
                    return prev;
                }
                var information = new DefaultServerInformation(lobby).snapshot(entry.getValue());
                if (!"LobbyV2".equals(information.displayNameString())) {
                    return List.of(information);
                }
                return parseV2(entry.getValue(), Collections.emptyList());
            });
            if (c != null) {
                for (var information : c) {
                    mergedInformations.put(information.uniqueId(), information);
                }
            }
        }
    }

    private @Nullable Collection<DefaultServerInformation> parseV2(ServiceInfoSnapshot snapshot, Collection<? extends ServerInformation> existing) {
        var extra = snapshot.readProperty(DarkCubeServiceProperty.EXTRA);
        if (extra == null) return Collections.emptyList();
        var lobbyV2 = extra.readDocument("LobbyV2");
        Map<String, DefaultServerInformation> map = new LinkedHashMap<>();
        keys:
        for (var key : lobbyV2.keys()) {
            for (var information : existing) {
                if (!(information instanceof DefaultServerInformationV2 v2)) continue;
                v2.snapshot(snapshot);
                if (v2.key().equals(key)) {
                    map.put(key, v2);
                    continue keys;
                }
            }
            map.put(key, new DefaultServerInformationV2(lobby, key).snapshot(snapshot));
        }
        if (map.isEmpty()) return null;
        return List.copyOf(map.values());
    }

    public void registerListener(UpdateListener updateListener) {
        this.updateListeners.add(updateListener);
    }

    public @Nullable ServerInformation byUniqueId(UUID uuid) {
        return mergedInformations.get(uuid);
    }

    CompletableFuture<ServerInformation.State> startConnectionRequest(UUID player, DefaultServerInformationV2 server) {
        var future = new CompletableFuture<ServerInformation.State>();
        var requestId = UUID.randomUUID();
        var request = new ConnectionRequest(future, player, server.snapshot.name());
        connectionRequests.put(requestId, request);
        ChannelMessage.builder().targetService(request.name()).channel("darkcube_lobbysystem_v2").message("start_connection_request").buffer(DataBuf.empty().writeUniqueId(requestId).writeUniqueId(player).writeObject(server.document())).build().send();
        return future;
    }

    public interface UpdateListener {
        void update(Collection<? extends ServerInformation> informations);
    }

    private record ConnectionRequest(CompletableFuture<ServerInformation.State> future, UUID player, String name) {
    }

    public static class ConnectionFailedException extends Exception {
        public ConnectionFailedException(String message) {
            super(message);
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    public class Listener {

        public Listener() {
            for (var service : InjectionLayer.boot().instance(CloudServiceProvider.class).services()) {
                services.put(service.serviceId().uniqueId(), service);
            }
            triggerUpdate();
        }

        @EventListener
        public void handle(ChannelMessageReceiveEvent event) {
            if (!event.channel().equals("darkcube_lobbysystem_v2")) return;
            if (event.message().equals("connection_request_status")) {
                var requestId = event.content().readUniqueId();
                var status = event.content().readInt();
                var request = connectionRequests.getIfPresent(requestId);
                if (request == null) return;
                if (status == 0) {
                    var message = event.content().readString();
                    request.future.complete(new ServerInformation.State(false, new ConnectionFailedException(message)));
                } else if (status == 2) {
                    request.future.complete(new ServerInformation.State(true, null));
                    connectionRequests.invalidate(requestId);
                    System.out.println("[ConnectorNPC] Connecting player " + request.player() + " to " + request.name());
                    lobby.playerManager().playerExecutor(request.player()).connect(request.name());
                }
            }
        }

        @EventListener
        public void handle(CloudServiceUpdateEvent event) {
            var service = event.serviceInfo();
            queue.add(() -> services.computeIfPresent(service.serviceId().uniqueId(), (ignoredUuid, ignoredServiceInfoSnapshot) -> service));
            triggerUpdate();
        }

        @EventListener
        public void handle(CloudServiceLifecycleChangeEvent event) {
            queue.add(() -> {
                if (event.newLifeCycle() == ServiceLifeCycle.RUNNING) {
                    services.put(event.serviceInfo().serviceId().uniqueId(), event.serviceInfo());
                    System.out.println("[ConnectorNPC] Server connected: " + event.serviceInfo().serviceId().name());
                } else if (event.lastLifeCycle() == ServiceLifeCycle.RUNNING) {
                    System.out.println("[ConnectorNPC] Server disconnected: " + event.serviceInfo().serviceId().name());
                    var snap = services.remove(event.serviceInfo().serviceId().uniqueId());
                    if (snap == null) System.err.println("Failed to remove from services!!!");
                }
            });
            triggerUpdate();
        }
    }

}
