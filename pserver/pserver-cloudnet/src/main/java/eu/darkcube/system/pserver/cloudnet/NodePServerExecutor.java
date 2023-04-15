/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import de.dytanic.cloudnet.CloudNet;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.common.logging.ILogger;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceConfiguration;
import de.dytanic.cloudnet.driver.service.ServiceDeployment;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

public class NodePServerExecutor implements PServerExecutor {
	private static final Key K_ACCESS_LEVEL = new Key("PServer", "accessLevel");
	private static final PersistentDataType<AccessLevel> T_ACCESS_LEVEL =
			PersistentDataTypes.enumType(AccessLevel.class);
	private static final Key K_TYPE = new Key("PServer", "type");
	private static final PersistentDataType<Type> T_TYPE = PersistentDataTypes.enumType(Type.class);
	private static final Key K_TASK = new Key("PServer", "task");
	private static final PersistentDataType<String> T_TASK = PersistentDataTypes.STRING;
	private static final ILogger logger = CloudNetDriver.getInstance().getLogger();
	private final UniqueId id;
	private final Set<UUID> owners = new CopyOnWriteArraySet<>();
	private final PServerStorage storage;
	private final AtomicReference<State> state = new AtomicReference<>(State.OFFLINE);
	private final Deque<ConnectionRequest> requestConnection = new ConcurrentLinkedDeque<>();
	private volatile int onlinePlayers = -1;
	private volatile String serverName = null;
	private volatile long startedAt = -1;
	private volatile ServiceInfoSnapshot snapshot;
	private volatile CompletableFuture<Void> stopFuture = CompletableFuture.completedFuture(null);

	public NodePServerExecutor(NodePServerProvider provider, UniqueId id) {
		this.id = id;
		this.storage = new PServerStorage(provider, id);

		owners.addAll(DatabaseProvider.get("pserver").cast(PServerDatabase.class).getOwners(id));

		JsonDocument doc = storage.storeToJsonDocument();
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
			storage.set(K_ACCESS_LEVEL, T_ACCESS_LEVEL,
					priv ? AccessLevel.PRIVATE : AccessLevel.PUBLIC);
			if (task != null) {
				storage.set(K_TASK, T_TASK, task);
				storage.set(K_TYPE, T_TYPE, Type.GAMEMODE);
			}
		}

		storage.setIfNotPresent(K_TYPE, T_TYPE, Type.WORLD);
		storage.addUpdateNotifier(s -> sendUpdate());
	}

	public NodePServerExecutor(NodePServerProvider provider, UniqueId id, Type type,
			String taskName) {
		this.id = id;
		this.storage = new PServerStorage(provider, id);
		this.storage.set(K_TYPE, T_TYPE, type);
		if (taskName != null)
			this.storage.set(K_TASK, T_TASK, taskName);
	}

	private void sendUpdate() {
		this.createSnapshot().thenAccept(s -> new PacketUpdate(s).sendAsync());
	}

	@Override
	public CompletableFuture<Boolean> start() {
		CompletableFuture<Boolean> fut = new CompletableFuture<>();
		if (state.compareAndSet(State.OFFLINE, State.STARTING)) {
			NodePServerProvider.instance().executor.execute(() -> {
				NodePServerProvider.instance().pservers.put(id, this);
			});
			startedAt = System.currentTimeMillis();
			new PacketStart(this.createSnapshot().getNow(null)).sendAsync();
			sendUpdate();
			String taskName = taskName().getNow(null);
			if (taskName == null)
				taskName = NodePServerProvider.pserverTaskName;
			ServiceConfiguration.Builder confb = ServiceConfiguration.builder()
					.task(Objects.requireNonNull(
							CloudNetDriver.getInstance().getServiceTaskProvider()
									.getServiceTask(taskName)))
					.task(NodePServerProvider.pserverTaskName).staticService(false)
					.addTemplates(NodePServerProvider.instance().globalTemplate())
					.addGroups("pserver-global");
			if (type().getNow(null).equals(Type.WORLD)) {
				confb.addGroups("pserver-world");
				confb.addTemplates(NodePServerProvider.instance().worldTemplate());
				ServiceTemplate template = PServerProvider.template(id);
				NodePServerProvider.instance().storage().create(template);
				confb.addTemplates(template);
				ServiceDeployment deployment = new ServiceDeployment(template,
						PServerModule.getInstance().getDeploymentExclusions());
				confb.addDeployments(deployment);
			}
			confb.node(CloudNet.getInstance().getConfig().getIdentity().getUniqueId());
			ServiceConfiguration configuration = confb.build();
			configuration.createNewServiceAsync().onComplete(snapshot -> {
				this.snapshot = snapshot;
				String serverName = snapshot.getName();
				snapshot.provider().startAsync().onComplete(v -> {
					this.serverName = serverName;
					sendUpdate();
					logger.info(
							"[PServer] Started PServer " + this.serverName + " (" + this.id + ")");
					fut.complete(true);
				}).onFailure(t -> {
					snapshot.provider().deleteAsync();
					startedAt = -1;
					this.snapshot = null;
					fut.completeExceptionally(t);
				}).onCancelled(t -> {
					snapshot.provider().deleteAsync();
					startedAt = -1;
					this.snapshot = null;
					fut.completeExceptionally(new CancellationException("Task cancelled!"));
				});
			}).onFailure(t -> {
				startedAt = -1;
				NodePServerProvider.instance().executor.execute(() -> {
					NodePServerProvider.instance().weakpservers.computeIfAbsent(id,
							id1 -> new SoftReference<>(this,
									NodePServerProvider.instance().referenceQueue));
					NodePServerProvider.instance().pservers.remove(id);
				});
				fut.completeExceptionally(t);
			}).onCancelled(t -> {
				startedAt = -1;
				NodePServerProvider.instance().executor.execute(() -> {
					NodePServerProvider.instance().weakpservers.computeIfAbsent(id,
							id1 -> new SoftReference<>(this,
									NodePServerProvider.instance().referenceQueue));
					NodePServerProvider.instance().pservers.remove(id);
				});
				fut.completeExceptionally(new CancellationException("Task cancelled!"));
			});
		} else {
			fut.complete(false);
		}
		return fut;
	}

	@Override
	public CompletableFuture<Void> stop() {
		CompletableFuture<Void> fut = new CompletableFuture<>();
		if (state.compareAndSet(State.STARTING, State.STOPPING)) {
			new PacketStop(this.createSnapshot().getNow(null)).sendAsync();
			serverName = null;
			sendUpdate();
			stopFuture = fut;
			logger.info("[PServer] Killing PServer " + serverName + " (" + id + ")");
			snapshot.provider().deleteAsync().onComplete(v -> {
				logger.info("[PServer] Killed PServer " + serverName + " (" + id + ")");
				snapshot = null;
				startedAt = -1;
				NodePServerProvider.instance().executor.execute(() -> {
					NodePServerProvider.instance().weakpservers.computeIfAbsent(id,
							id1 -> new SoftReference<>(this,
									NodePServerProvider.instance().referenceQueue));
					NodePServerProvider.instance().pservers.remove(id);
				});
			}).onFailure(fut::completeExceptionally).onCancelled(
					t -> fut.completeExceptionally(new CancellationException("Task cancelled!")));
		} else if (state.compareAndSet(State.RUNNING, State.STOPPING)) {
			new PacketStop(this.createSnapshot().getNow(null)).sendAsync();
			serverName = null;
			sendUpdate();
			stopFuture = fut;
			logger.info("[PServer] Stopping PServer " + serverName + " (" + id + ")");
			snapshot.provider().stopAsync().onComplete(v -> {
				snapshot.provider().deleteAsync().onComplete(v2 -> {
					logger.info("[PServer] Stopped PServer " + serverName + " (" + id + ")");
					if (!state.compareAndSet(State.STOPPING, State.OFFLINE)) {
						logger.error("[PServer] PServer was stopping but state wasn't stopping");
					} else {
						NodePServerProvider.instance().executor.execute(() -> {
							NodePServerProvider.instance().weakpservers.computeIfAbsent(id,
									id1 -> new SoftReference<>(this,
											NodePServerProvider.instance().referenceQueue));
							NodePServerProvider.instance().pservers.remove(id);
						});
						sendUpdate();
					}
					startedAt = -1;
					snapshot = null;
					fut.complete(null);
				}).onFailure(fut::completeExceptionally).onCancelled(t -> fut.completeExceptionally(
						new CancellationException("Task cancelled!")));
			}).onFailure(fut::completeExceptionally).onCancelled(
					t -> fut.completeExceptionally(new CancellationException("Task cancelled!")));
		} else {
			return stopFuture;
		}
		return fut;
	}

	@Override
	public CompletableFuture<Boolean> accessLevel(AccessLevel level) {
		return storage().setAsync(K_ACCESS_LEVEL, T_ACCESS_LEVEL, level).thenApply(p -> true);
	}

	@Override
	public CompletableFuture<Boolean> addOwner(UUID uuid) {
		if (owners.add(uuid)) {
			DatabaseProvider.get("pserver").cast(PServerDatabase.class).update(id, uuid);
			new PacketAddOwner(id, uuid).sendAsync();
			sendUpdate();
		}
		return CompletableFuture.completedFuture(true);
	}

	@Override
	public CompletableFuture<Boolean> removeOwner(UUID uuid) {
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
		return CompletableFuture.completedFuture(
				new PServerSnapshot(id, state.get(), onlinePlayers, startedAt, serverName,
						owners.toArray(new UUID[0])));
	}

	@Override
	public @NotNull CompletableFuture<Boolean> connectPlayer(UUID player) {
		State state = this.state.get();
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
		return CompletableFuture.completedFuture(state.get());
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
	public @NotNull PServerStorage storage() {
		return storage;
	}

	@Override
	public CompletableFuture<Long> startedAt() {
		return CompletableFuture.completedFuture(startedAt);
	}

	@Override
	public CompletableFuture<Long> ontime() {
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
		return CompletableFuture.completedFuture(
				Collections.unmodifiableCollection(new HashSet<>(owners)));
	}

	@Override
	public @NotNull CompletableFuture<@Nullable String> taskName() {
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
			if (requestConnection.isEmpty() || serverName == null)
				return;
			IPlayerManager pm = CloudNetDriver.getInstance().getServicesRegistry()
					.getFirstService(IPlayerManager.class);
			ConnectionRequest request;
			while ((request = requestConnection.poll()) != null) {
				pm.getPlayerExecutor(request.player).connect(serverName);
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

	private static class ConnectionRequest {
		private final UUID player;
		private final CompletableFuture<Boolean> future;

		public ConnectionRequest(UUID player, CompletableFuture<Boolean> future) {
			this.player = player;
			this.future = future;
		}
	}

}
