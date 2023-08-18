/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceDeployment;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.node.cluster.NodeServerProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.PServerSnapshot;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.nw.*;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class NodePServerExecutor implements PServerExecutor {
    private static final Key K_ACCESS_LEVEL = new Key("PServer", "accessLevel");
    private static final PersistentDataType<AccessLevel> T_ACCESS_LEVEL = PersistentDataTypes.enumType(AccessLevel.class);
    private static final Key K_TYPE = new Key("PServer", "type");
    private static final PersistentDataType<Type> T_TYPE = PersistentDataTypes.enumType(Type.class);
    private static final Key K_TASK = new Key("PServer", "task");
    private static final PersistentDataType<String> T_TASK = PersistentDataTypes.STRING;
    private static final Logger logger = Logger.getGlobal();
    private final UniqueId id;
    private final Set<UUID> owners = new CopyOnWriteArraySet<>();
    private final PServerStorage storage;
    private final AtomicReference<State> state = new AtomicReference<>(State.OFFLINE);
    private final Deque<ConnectionRequest> requestConnection = new ConcurrentLinkedDeque<>();
    private ServiceTaskProvider serviceTaskProvider = InjectionLayer.boot().instance(ServiceTaskProvider.class);
    private NodeServerProvider nodeServerProvider = InjectionLayer.boot().instance(NodeServerProvider.class);
    private PlayerManager playerManager = InjectionLayer.boot().instance(ServiceRegistry.class).firstProvider(PlayerManager.class);
    private volatile int onlinePlayers = -1;
    private volatile String serverName = null;
    private volatile long startedAt = -1;
    private volatile ServiceInfoSnapshot snapshot;
    private volatile CompletableFuture<Void> stopFuture = CompletableFuture.completedFuture(null);

    public NodePServerExecutor(NodePServerProvider provider, UniqueId id) {
        this.id = id;
        this.storage = new PServerStorage(provider, id);

        owners.addAll(DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(id));

        Document.Mutable doc = storage.storeToJsonDocument().mutableCopy();
        if (doc.contains("private")) {
            boolean priv = doc.getBoolean("private");
            doc.remove("private");
            String task = doc.getString("task");
            if (task != null && !storage.has(K_TYPE)) {
                doc.remove("task");
            } else {
                task = null;
            }
            doc.remove("startedBy");
            storage.loadFromJsonDocument(doc);
            storage.set(K_ACCESS_LEVEL, T_ACCESS_LEVEL, priv ? AccessLevel.PRIVATE : AccessLevel.PUBLIC);
            if (task != null) {
                storage.set(K_TASK, T_TASK, task);
                storage.set(K_TYPE, T_TYPE, Type.GAMEMODE);
            }
        }

        storage.setIfNotPresent(K_TYPE, T_TYPE, Type.WORLD);
        storage.addUpdateNotifier(s -> sendUpdate());
    }

    public NodePServerExecutor(@NotNull NodePServerProvider provider, @NotNull UniqueId id, @NotNull Type type, @NotNull String taskName) {
        this.id = id;
        this.storage = new PServerStorage(provider, id);
        this.storage.set(K_TYPE, T_TYPE, type);
        this.storage.set(K_TASK, T_TASK, taskName);
    }

    private void sendUpdate() {
        this.createSnapshot().thenAccept(s -> new PacketUpdate(s).sendAsync());
    }

    @Override public @NotNull CompletableFuture<Boolean> start() {
        CompletableFuture<Boolean> fut = new CompletableFuture<>();
        if (state.compareAndSet(State.OFFLINE, State.STARTING)) {
            NodePServerProvider.instance().executor.execute(() -> {
                NodePServerProvider.instance().pservers.put(id, this);
            });
            startedAt = System.currentTimeMillis();
            new PacketStart(this.createSnapshot().getNow(null)).sendAsync();
            sendUpdate();
            String taskName = taskName().getNow(null);
            if (taskName == null) taskName = NodePServerProvider.pserverTaskName;
            ServiceConfiguration.Builder confb = ServiceConfiguration
                    .builder(serviceTaskProvider.serviceTask(taskName))
                    .taskName(NodePServerProvider.pserverTaskName)
                    .staticService(false)
                    .modifyTemplates(templates -> templates.add(NodePServerProvider.instance().globalTemplate()))
                    .modifyGroups(g -> g.add("pserver-global"));
            if (type().join().equals(Type.WORLD)) {
                confb.modifyGroups(g -> g.add("pserver-world"));
                confb.modifyTemplates(t -> t.add(NodePServerProvider.instance().worldTemplate()));
                ServiceTemplate template = PServerProvider.template(id);
                NodePServerProvider.instance().storage().create(template);
                confb.modifyTemplates(t -> t.add(template));
                ServiceDeployment deployment = ServiceDeployment
                        .builder()
                        .template(template)
                        .excludes(PServerModule.getInstance().compiledDeploymentExclusions())
                        .build();
                confb.deployments(Collections.singleton(deployment));
            }
            confb.node(nodeServerProvider.localNode().info().uniqueId());
            ServiceConfiguration configuration = confb.build();
            configuration.createNewServiceAsync().thenAccept(result -> {
                this.snapshot = result.serviceInfo();
                String serverName = snapshot.name();
                snapshot.provider().startAsync().thenRun(() -> {
                    this.serverName = serverName;
                    sendUpdate();
                    logger.info("[PServer] Started PServer " + this.serverName + " (" + this.id + ")");
                    fut.complete(true);
                }).exceptionally(t -> {
                    snapshot.provider().deleteAsync();
                    startedAt = -1;
                    this.snapshot = null;
                    fut.completeExceptionally(t);
                    return null;
                });
            }).exceptionally(t -> {
                startedAt = -1;
                NodePServerProvider.instance().executor.execute(() -> {
                    NodePServerProvider.instance().weakpservers.computeIfAbsent(id, id1 -> new SoftReference<>(this, NodePServerProvider.instance().referenceQueue));
                    NodePServerProvider.instance().pservers.remove(id);
                });
                fut.completeExceptionally(t);
                return null;
            });
        } else {
            fut.complete(false);
        }
        return fut;
    }

    @Override public @NotNull CompletableFuture<Void> stop() {
        CompletableFuture<Void> fut = new CompletableFuture<>();
        if (state.compareAndSet(State.STARTING, State.STOPPING)) {
            new PacketStop(this.createSnapshot().getNow(null)).sendAsync();
            serverName = null;
            sendUpdate();
            stopFuture = fut;
            logger.info("[PServer] Killing PServer " + serverName + " (" + id + ")");
            snapshot.provider().deleteAsync().thenRun(() -> {
                logger.info("[PServer] Killed PServer " + serverName + " (" + id + ")");
                snapshot = null;
                startedAt = -1;
                NodePServerProvider.instance().executor.execute(() -> {
                    NodePServerProvider.instance().weakpservers.computeIfAbsent(id, id1 -> new SoftReference<>(this, NodePServerProvider.instance().referenceQueue));
                    NodePServerProvider.instance().pservers.remove(id);
                });
            }).exceptionally(t -> {
                fut.completeExceptionally(t);
                return null;
            });
        } else if (state.compareAndSet(State.RUNNING, State.STOPPING)) {
            new PacketStop(this.createSnapshot().getNow(null)).sendAsync();
            serverName = null;
            sendUpdate();
            stopFuture = fut;
            logger.info("[PServer] Stopping PServer " + serverName + " (" + id + ")");
            snapshot.provider().stopAsync().thenRun(() -> snapshot.provider().deleteAsync().thenRun(() -> {
                logger.info("[PServer] Stopped PServer " + serverName + " (" + id + ")");
                if (!state.compareAndSet(State.STOPPING, State.OFFLINE)) {
                    logger.severe("[PServer] PServer was stopping but state wasn't stopping");
                } else {
                    NodePServerProvider.instance().executor.execute(() -> {
                        NodePServerProvider.instance().weakpservers.computeIfAbsent(id, id1 -> new SoftReference<>(this, NodePServerProvider.instance().referenceQueue));
                        NodePServerProvider.instance().pservers.remove(id);
                    });
                    sendUpdate();
                }
                startedAt = -1;
                snapshot = null;
                fut.complete(null);
            }).exceptionally(t -> {
                fut.completeExceptionally(t);
                return null;
            })).exceptionally(t -> {
                fut.completeExceptionally(t);
                return null;
            });
        } else {
            return stopFuture;
        }
        return fut;
    }

    @Override public @NotNull CompletableFuture<Boolean> accessLevel(@NotNull AccessLevel level) {
        return storage().setAsync(K_ACCESS_LEVEL, T_ACCESS_LEVEL, level).thenApply(p -> true);
    }

    @Override public @NotNull CompletableFuture<Boolean> addOwner(UUID uuid) {
        if (owners.add(uuid)) {
            DatabaseProvider.get("pserver").cast(PServerDatabase.class).update(id, uuid);
            new PacketAddOwner(id, uuid).sendAsync();
            sendUpdate();
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override public @NotNull CompletableFuture<Boolean> removeOwner(UUID uuid) {
        if (owners.remove(uuid)) {
            DatabaseProvider.get("pserver").cast(PServerDatabase.class).delete(id, uuid);
            new PacketRemoveOwner(id, uuid).sendAsync();
            sendUpdate();
            if (owners.isEmpty()) {
                delete();
            }
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override public @NotNull CompletableFuture<PServerSnapshot> createSnapshot() {
        return CompletableFuture.completedFuture(new PServerSnapshot(id, state.get(), onlinePlayers, startedAt, serverName, owners.toArray(new UUID[0])));
    }

    @Override public @NotNull CompletableFuture<Boolean> connectPlayer(UUID player) {
        State state = this.state.get();
        if (state == State.STARTING || state == State.RUNNING) {
            CompletableFuture<Boolean> fut = new CompletableFuture<>();
            requestConnection.offer(new ConnectionRequest(player, fut));
            workRequestedConnections();
            return fut;
        }
        return CompletableFuture.completedFuture(false);
    }

    @Override public @NotNull UniqueId id() {
        return id;
    }

    @Override public @NotNull CompletableFuture<State> state() {
        return CompletableFuture.completedFuture(state.get());
    }

    @Override public @NotNull CompletableFuture<Type> type() {
        return storage.getAsync(K_TYPE, T_TYPE);
    }

    @Override public @NotNull CompletableFuture<AccessLevel> accessLevel() {
        return storage.getAsync(K_ACCESS_LEVEL, T_ACCESS_LEVEL, () -> AccessLevel.PUBLIC);
    }

    @Override public @NotNull PServerStorage storage() {
        return storage;
    }

    @Override public @NotNull CompletableFuture<Long> startedAt() {
        return CompletableFuture.completedFuture(startedAt);
    }

    @Override public @NotNull CompletableFuture<Long> ontime() {
        return startedAt().thenApply(s -> System.currentTimeMillis() - s);
    }

    @Override public @NotNull CompletableFuture<Integer> onlinePlayers() {
        return CompletableFuture.completedFuture(onlinePlayers);
    }

    @Override public @NotNull CompletableFuture<String> serverName() {
        return CompletableFuture.completedFuture(serverName);
    }

    @Override public @NotNull CompletableFuture<@UnmodifiableView @NotNull Collection<@NotNull UUID>> owners() {
        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(new HashSet<>(owners)));
    }

    @Override public @NotNull CompletableFuture<@Nullable String> taskName() {
        return storage.getAsync(K_TASK, T_TASK);
    }

    private CompletableFuture<Void> delete() {
        return stop().thenRunAsync(() -> {
            try {
                Type type = type().get();
                if (type == Type.WORLD) {
                    ServiceTemplate template = PServerProvider.template(id);
                    NodePServerProvider.instance().storage().delete(template);
                    NodePServerProvider.instance().pserverData().delete(id.toString());
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void workRequestedConnections() {
        State state = this.state.get();
        if (state == State.RUNNING) {
            String serverName = this.serverName;
            if (requestConnection.isEmpty() || serverName == null) return;
            ConnectionRequest request;
            while ((request = requestConnection.poll()) != null) {
                playerManager.playerExecutor(request.player).connect(serverName);
                request.future.complete(true);
            }
        } else if (state == State.STOPPING || state == State.OFFLINE) {
            ConnectionRequest request;
            while ((request = requestConnection.poll()) != null) {
                request.future.complete(false);
            }
        }
    }

    public void setOnlinePlayers(int onlinePlayers) {
        this.onlinePlayers = onlinePlayers;
        sendUpdate();
    }

    public void setRunning() {
        state.compareAndSet(State.STARTING, State.RUNNING);
        workRequestedConnections();
    }

    private record ConnectionRequest(UUID player, CompletableFuture<Boolean> future) {
    }
}
