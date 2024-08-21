/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.cloudnetservice.driver.provider.ServiceTaskProvider;
import eu.cloudnetservice.driver.registry.ServiceRegistry;
import eu.cloudnetservice.driver.service.ServiceConfiguration;
import eu.cloudnetservice.driver.service.ServiceCreateResult;
import eu.cloudnetservice.driver.service.ServiceDeployment;
import eu.cloudnetservice.driver.service.ServiceInfoSnapshot;
import eu.cloudnetservice.driver.service.ServiceTask;
import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.cloudnetservice.modules.bridge.player.PlayerManager;
import eu.cloudnetservice.node.cluster.NodeServerProvider;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
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
import eu.darkcube.system.pserver.common.packets.nw.PacketAddOwner;
import eu.darkcube.system.pserver.common.packets.nw.PacketRemoveOwner;
import eu.darkcube.system.pserver.common.packets.nw.PacketStart;
import eu.darkcube.system.pserver.common.packets.nw.PacketStop;
import eu.darkcube.system.pserver.common.packets.nw.PacketUpdate;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodePServerExecutor implements PServerExecutor {
    private static final Key K_ACCESS_LEVEL = Key.key("pserver", "access_level");
    private static final PersistentDataType<AccessLevel> T_ACCESS_LEVEL = PersistentDataTypes.enumType(AccessLevel.class);
    private static final Key K_TYPE = Key.key("pserver", "type");
    private static final PersistentDataType<Type> T_TYPE = PersistentDataTypes.enumType(Type.class);
    private static final Key K_TASK = Key.key("pserver", "task");
    private static final PersistentDataType<String> T_TASK = PersistentDataTypes.STRING;
    private static final Logger LOGGER = LoggerFactory.getLogger("PServer");
    private final UniqueId id;
    private final Set<UUID> owners = new CopyOnWriteArraySet<>();
    private final PersistentDataStorage storage;
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
        this.storage = createStorage(id);

        owners.addAll(DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(id));

        var doc = storage.storeToJsonObject();
        if (doc.has("private")) {
            var privateServer = doc.get("private").getAsBoolean();
            doc.remove("private");
            var task = doc.get("task").getAsString();
            if (task != null && !storage.has(K_TYPE)) {
                doc.remove("task");
            } else {
                task = null;
            }
            doc.remove("startedBy");
            storage.loadFromJsonObject(doc);
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
        this.storage = createStorage(id);
        this.storage.set(K_TYPE, T_TYPE, type);
        this.storage.set(K_TASK, T_TASK, taskName);
    }

    private PersistentDataStorage createStorage(UniqueId id) {
        // noinspection PatternValidation
        return CustomPersistentDataProvider.dataProvider().persistentData("pserver_data", Key.key("", id.toString()));
    }

    /**
     * Sends an update to all wrappers
     */
    private void sendUpdate() {
        new PacketUpdate(createSnapshotInternal()).sendAsync();
    }

    @Override
    public @NotNull CompletableFuture<Boolean> start() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                lock.lock();

                if (state != State.OFFLINE) return false;

                var taskName = taskName().join();
                if (taskName == null) taskName = NodePServerProvider.pserverTaskName;
                @Nullable var task = serviceTaskProvider.serviceTask(taskName);
                if (task == null) {
                    LOGGER.warn("Task not found for PServer creation: {}", taskName);
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
                LOGGER.info("[PServer] Started PServer {} ({})", this.serverName, this.id);
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
        LOGGER.info("[PServer] Stopping PServer {} ({})", serverName, id);
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
                LOGGER.info("[PServer] Stopped PServer {} ({})", serverName, id);
                if (state != State.STOPPING) LOGGER.error("[PServer] PServer was stopping but state wasn't stopping");
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
        LOGGER.info("[PServer] Killing PServer {} ({})", serverName, id);
        stopFuture = snapshot.provider().deleteAsync().thenRun(() -> {
            try {
                lock.lock();
                LOGGER.info("[PServer] Killed PServer {} ({})", serverName, id);
                resetState();
                NodePServerProvider.instance().releaseReference(this);
            } finally {
                lock.unlock();
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Void> stop() {
        return CompletableFuture.supplyAsync(() -> {
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

    @Override
    public @NotNull CompletableFuture<Boolean> accessLevel(@NotNull AccessLevel level) {
        return storage().setAsync(K_ACCESS_LEVEL, T_ACCESS_LEVEL, level).thenApply(p -> true);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> addOwner(UUID uuid) {
        if (owners.add(uuid)) {
            DatabaseProvider.get("pserver").cast(PServerDatabase.class).update(id, uuid);
            new PacketAddOwner(id, uuid).sendAsync();
            sendUpdate();
        }
        return CompletableFuture.completedFuture(true);
    }

    @Override
    public @NotNull CompletableFuture<Boolean> removeOwner(UUID uuid) {
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

    @Override
    public @NotNull CompletableFuture<PServerSnapshot> createSnapshot() {
        return CompletableFuture.completedFuture(createSnapshotInternal());
    }

    private PServerSnapshot createSnapshotInternal() {
        return new PServerSnapshot(id, state, onlinePlayers, startedAt, serverName, owners.toArray(new UUID[0]));
    }

    @Override
    public @NotNull CompletableFuture<Boolean> connectPlayer(UUID player) {
        State state = this.state;
        if (state == State.STARTING || state == State.RUNNING) {
            CompletableFuture<Boolean> fut = new CompletableFuture<>();
            requestConnection.offer(new ConnectionRequest(player, fut));
            workRequestedConnections();
            return fut;
        }
        return CompletableFuture.completedFuture(false);
    }

    @Override
    public @NotNull UniqueId id() {
        return id;
    }

    @Override
    public @NotNull CompletableFuture<State> state() {
        return CompletableFuture.completedFuture(state);
    }

    @Override
    public @NotNull CompletableFuture<Type> type() {
        return storage.getAsync(K_TYPE, T_TYPE);
    }

    @Override
    public @NotNull CompletableFuture<AccessLevel> accessLevel() {
        return storage.getAsync(K_ACCESS_LEVEL, T_ACCESS_LEVEL, () -> AccessLevel.PUBLIC);
    }

    @Override
    public @NotNull PersistentDataStorage storage() {
        return storage;
    }

    @Override
    public @NotNull CompletableFuture<Long> startedAt() {
        return CompletableFuture.completedFuture(startedAt);
    }

    @Override
    public @NotNull CompletableFuture<Long> ontime() {
        return startedAt().thenApply(s -> System.currentTimeMillis() - s);
    }

    @Override
    public @NotNull CompletableFuture<Integer> onlinePlayers() {
        return CompletableFuture.completedFuture(onlinePlayers);
    }

    @Override
    public @NotNull CompletableFuture<String> serverName() {
        return CompletableFuture.completedFuture(serverName);
    }

    @Override
    public @NotNull CompletableFuture<@UnmodifiableView @NotNull Collection<@NotNull UUID>> owners() {
        return CompletableFuture.completedFuture(Collections.unmodifiableCollection(new HashSet<>(owners)));
    }

    @Deprecated
    @ApiStatus.Internal
    @Override
    public @NotNull CompletableFuture<@Nullable String> taskName() {
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
        var builder = ServiceConfiguration.builder(task).taskName(NodePServerProvider.pserverTaskName).staticService(false).modifyTemplates(templates -> templates.add(NodePServerProvider.instance().globalTemplate())).modifyGroups(g -> g.add("pserver-global"));
        return type().thenApply(type -> {
            if (type == Type.WORLD) {
                builder.modifyGroups(g -> g.add("pserver-world"));
                builder.modifyTemplates(t -> t.add(NodePServerProvider.instance().worldTemplate()));
                var template = PServerProvider.template(id);
                NodePServerProvider.instance().storage().create(template);
                builder.modifyTemplates(t -> t.add(template));
                var deployment = ServiceDeployment.builder().template(template).excludes(PServerModule.getInstance().compiledDeploymentExclusions()).build();
                builder.deployments(Collections.singleton(deployment));
            }
            builder.node(nodeServerProvider.localNode().info().uniqueId());
            return builder.build();
        });
    }

    private record ConnectionRequest(UUID player, CompletableFuture<Boolean> future) {
    }
}
