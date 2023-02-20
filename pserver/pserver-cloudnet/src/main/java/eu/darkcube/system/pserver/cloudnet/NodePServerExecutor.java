/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import de.dytanic.cloudnet.common.logging.ILogger;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerSnapshot;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.nw.PacketUpdate;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;

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
	private volatile State state = State.OFFLINE;
	private volatile int onlinePlayers = -1;
	private volatile String serverName = null;
	private volatile long startedAt = -1;

	public NodePServerExecutor(NodePServerProvider provider, UniqueId id) {
		this.id = id;
		this.storage = new PServerStorage(provider, id);
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
		return null;
	}

	@Override
	public CompletableFuture<Void> stop() {
		return null;
	}

	@Override
	public CompletableFuture<Boolean> accessLevel(AccessLevel level) {
		return storage().setAsync(K_ACCESS_LEVEL, T_ACCESS_LEVEL, level).thenApply(p -> true);
	}

	@Override
	public CompletableFuture<Boolean> addOwner(UUID uuid) {
		if (owners.add(uuid)) {
			sendUpdate();
		}
		return CompletableFuture.completedFuture(true);
	}

	@Override
	public CompletableFuture<Boolean> removeOwner(UUID uuid) {
		if (owners.remove(uuid)) {
			sendUpdate();
		}
		return CompletableFuture.completedFuture(true);
	}

	@Override
	public @NotNull CompletableFuture<PServerSnapshot> createSnapshot() {
		return CompletableFuture.completedFuture(
				new PServerSnapshot(id, state, onlinePlayers, startedAt, serverName,
						owners.toArray(new UUID[0])));
	}

	@Override
	public @NotNull CompletableFuture<Boolean> connectPlayer(UUID player) {// TODO
		return null;
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
}
