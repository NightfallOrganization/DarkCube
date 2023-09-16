/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.*;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.node.cluster.NodeServerProvider;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
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

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    private final Deque<ConnectionRequest> requestConnection = new ConcurrentLinkedDeque<>();
    private final ServiceTaskProvider serviceTaskProvider = InjectionLayer.boot().instance(ServiceTaskProvider.class);
    private final NodeServerProvider nodeServerProvider = InjectionLayer.boot().instance(NodeServerProvider.class);
    private final PlayerManager playerManager = InjectionLayer.boot().instance(ServiceRegistry.class).firstProvider(PlayerManager.class);
    private final Lock lock = new ReentrantLock();
    private volatile State state = State.OFFLINE;
    private volatile int onlinePlayers = -1;
    private volatile String serverName = null;
    private volatile long startedAt = -1;
    private volatile ServiceInfoSnapshot snapshot;
    private CompletableFuture<Void> stopFuture = null;

    public NodePServerExecutor(NodePServerProvider provider, UniqueId id) {
        this.id = id;
        this.storage = new PServerStorage(provider, this);

        owners.addAll(DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(id));

        Document.Mutable doc = storage.storeToJsonDocument().mutableCopy();
        if (doc.contains("private")) {
            var privateServer = doc.getBoolean("private");
            doc.remove("private");
            var task = doc.getString("task");
            if (task != null && !storage.has(K_TYPE)) {
                doc.remove("task");
            } else {
                task = null;
            }
            doc.remove("startedBy");
            storage.loadFromJsonDocument(doc);
            storage.set(K_ACCESS_LEVEL, T_ACCESS_LEVEL, privateServer ? AccessLevel.PRIVATE : AccessLevel.PUBLIC);
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
        this.storage = new PServerStorage(provider, this);
        this.storage.set(K_TYPE, T_TYPE, type);
        this.storage.set(K_TASK, T_TASK, taskName);
    }

    /**
     * Sends an update to all wrappers
     */
    private void sendUpdate() {
        new PacketUpdate(createSnapshotInternal()).sendAsync();
    }

    @Override public @NotNull CompletableFuture<Boolean> start() {
        return Task.supply(() -> {
            try {
                lock.lock();

                if (state != State.OFFLINE) return false;

                var taskName = taskName().getNow(null);
                if (taskName == null) taskName = NodePServerProvider.pserverTaskName;
                @Nullable var task = serviceTaskProvider.serviceTask(taskName);
                if (task == null) {
                    logger.warning("Task not found for PServer creation: " + taskName);
                    return false;
                }
                var serviceSnapshot = createService(task).join();

                if (serviceSnapshot == null) return false;

                NodePServerProvider.instance().holdReference(this);

                state = State.STARTING;
                startedAt = System.currentTimeMillis();
                snapshot = serviceSnapshot;
                serverName = snapshot.name();
                sendUpdate();
                logger.info("[PServer] Started PServer " + this.serverName + " (" + this.id + ")");
                snapshot.provider().startAsync().whenComplete((unused, throwable) -> {
                    if (throwable != null) {
                        throwable.printStackTrace();
                        resetState();
                    }
                });

                new PacketStart(createSnapshotInternal()).sendAsync();
                return true;
            } finally {
                lock.unlock();
            }
        });
    }

    private void resetState() {
        try {
            lock.lock();
            startedAt = -1;
            state = State.OFFLINE;
            snapshot = null;
            serverName = null;
            sendUpdate();
        } finally {
            lock.unlock();
        }
    }

    private void stopRunningServer() {
        state = State.STOPPING;
        sendUpdate();
        new PacketStop(createSnapshotInternal()).sendAsync();
        logger.info("[PServer] Stopping PServer " + serverName + " (" + id + ")");
        stopFuture = snapshot.provider().stopAsync().thenCompose(unused -> {
            try {
                lock.lock();
                return snapshot.provider().deleteAsync();
            } finally {
                lock.unlock();
            }
        }).thenRun(() -> {
            try {
                lock.lock();
                logger.info("[PServer] Stopped PServer " + serverName + " (" + id + ")");
                if (state != State.STOPPING) logger.severe("[PServer] PServer was stopping but state wasn't stopping");
                resetState();
                NodePServerProvider.instance().releaseReference(this);
            } finally {
                lock.unlock();
            }
        });
    }

    private void killStartingServer() {
        state = State.STOPPING;
        sendUpdate();
        new PacketStop(createSnapshotInternal()).sendAsync();
        logger.info("[PServer] Killing PServer " + serverName + " (" + id + ")");
        stopFuture = snapshot.provider().deleteAsync().thenRun(() -> {
            try {
                lock.lock();
                logger.info("[PServer] Killed PServer " + serverName + " (" + id + ")");
                resetState();
                NodePServerProvider.instance().releaseReference(this);
            } finally {
                lock.unlock();
            }
        });
    }

    @Override public @NotNull CompletableFuture<Void> stop() {
        return Task.supply(() -> {
            try {
                lock.lock();
                if (state == State.STARTING) {
                    killStartingServer();
                } else if (state == State.RUNNING) {
                    stopRunningServer();
                } else {
                    return stopFuture;
                }
                return stopFuture;
            } finally {
                lock.unlock();
            }
        }).thenCompose(future -> future);
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
        return CompletableFuture.completedFuture(createSnapshotInternal());
    }

    private PServerSnapshot createSnapshotInternal() {
        return new PServerSnapshot(id, state, onlinePlayers, startedAt, serverName, owners.toArray(new UUID[0]));
    }

    @Override public @NotNull CompletableFuture<Boolean> connectPlayer(UUID player) {
        State state = this.state;
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
        return CompletableFuture.completedFuture(state);
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

    @Deprecated @ApiStatus.Internal @Override public @NotNull CompletableFuture<@Nullable String> taskName() {
        return storage.getAsync(K_TASK, T_TASK);
    }

    private CompletableFuture<Void> delete() {
        return stop().thenRunAsync(() -> {
            var type = type().join();
            if (type == Type.WORLD) {
                ServiceTemplate template = PServerProvider.template(id);
                NodePServerProvider.instance().storage().delete(template);
                NodePServerProvider.instance().pserverData().delete(id.toString());
            }
        });
    }

    private void workRequestedConnections() {
        final var state = this.state;
        if (state == State.RUNNING) {
            var serverName = this.serverName;
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
        try {
            lock.lock();
            if (state == State.STARTING) state = State.RUNNING;
        } finally {
            lock.unlock();
        }
        workRequestedConnections();
    }

    private CompletableFuture<ServiceInfoSnapshot> createService(ServiceTask task) {
        return createConfiguration(task).thenCompose(ServiceConfiguration::createNewServiceAsync).thenApply(result -> {
            if (result.state() == ServiceCreateResult.State.CREATED) {
                return result.serviceInfo();
            }
            result.serviceInfo().provider().stop();
            return null;
        });
    }

    private CompletableFuture<ServiceConfiguration> createConfiguration(ServiceTask task) {
        var builder = ServiceConfiguration
                .builder(task)
                .taskName(NodePServerProvider.pserverTaskName)
                .staticService(false)
                .modifyTemplates(templates -> templates.add(NodePServerProvider.instance().globalTemplate()))
                .modifyGroups(g -> g.add("pserver-global"));
        return type().thenApply(type -> {
            if (type == Type.WORLD) {
                builder.modifyGroups(g -> g.add("pserver-world"));
                builder.modifyTemplates(t -> t.add(NodePServerProvider.instance().worldTemplate()));
                var template = PServerProvider.template(id);
                NodePServerProvider.instance().storage().create(template);
                builder.modifyTemplates(t -> t.add(template));
                var deployment = ServiceDeployment
                        .builder()
                        .template(template)
                        .excludes(PServerModule.getInstance().compiledDeploymentExclusions())
                        .build();
                builder.deployments(Collections.singleton(deployment));
            }
            builder.node(nodeServerProvider.localNode().info().uniqueId());
            return builder.build();
        });
    }

    private record ConnectionRequest(UUID player, CompletableFuture<Boolean> future) {
    }
}
