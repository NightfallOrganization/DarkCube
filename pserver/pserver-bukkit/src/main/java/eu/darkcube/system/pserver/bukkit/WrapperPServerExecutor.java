/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import eu.darkcube.system.packetapi.Packet;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerSnapshot;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.*;
import eu.darkcube.system.util.data.PersistentDataStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class WrapperPServerExecutor implements PServerExecutor {

	private final UniqueId id;
	private volatile State state;
	private volatile Type type;
	private volatile AccessLevel accessLevel;
	private volatile long startedAt;
	private volatile Collection<UUID> owners;
	private volatile String serverName;
	private volatile int onlinePlayers;
	private volatile String taskName;
	private volatile PersistentDataStorage storage;

	WrapperPServerExecutor(UniqueId id) {
		this.id = id;
	}

	public void update(PServerSnapshot info) {
		this.state = info.state();
		this.onlinePlayers = info.onlinePlayers();
		this.type = info.type();
		this.startedAt = info.startedAt();
		this.taskName = info.taskName();
		this.accessLevel = info.accessLevel();
		this.serverName = info.serverName();
	}

	@Override
	public State state() {
		return state;
	}

	private <T, V extends Packet> CompletableFuture<T> future(Packet packet, Class<V> returnClass,
			Function<V, T> mapper) {
		CompletableFuture<T> fut = new CompletableFuture<>();
		packet.sendQueryAsync(returnClass).onComplete(p -> fut.complete(mapper.apply(p)))
				.onCancelled(t -> fut.completeExceptionally(
						new CancellationException("Task " + "cancelled")))
				.onFailure(fut::completeExceptionally);
		return fut;
	}

	private CompletableFuture<Void> voidFuture(Packet packet) {
		return future(packet, PacketNodeWrapperActionConfirm.class, p -> null);
	}

	private CompletableFuture<Boolean> booleanFuture(Packet packet) {
		return future(packet, PacketNodeWrapperBoolean.class, PacketNodeWrapperBoolean::is);
	}

	@Override
	public CompletableFuture<Boolean> start() {
		return booleanFuture(new PacketWrapperNodeStart(id));
	}

	@Override
	public CompletableFuture<Void> stop() {
		return this.voidFuture(new PacketWrapperNodeStop(id));
	}

	@Override
	public CompletableFuture<Boolean> connectPlayer(UUID player) {
		return booleanFuture(new PacketWrapperNodeConnectPlayer(player, id));
	}

	@Override
	public int onlinePlayers() {
		return onlinePlayers;
	}

	@Override
	public Type type() {
		return type;
	}

	@Override
	public AccessLevel accessLevel() {
		return accessLevel;
	}

	@Override
	public CompletableFuture<Boolean> accessLevel(AccessLevel level) {
		return booleanFuture(new PacketWrapperNodeAccessLevel(accessLevel));
	}

	@Override
	public UniqueId id() {
		return id;
	}

	@Override
	public long ontime() {
		return System.currentTimeMillis() - startedAt();
	}

	@Override
	public Collection<UUID> owners() {
		return Collections.unmodifiableCollection(owners);
	}

	@Override
	public String serverName() {
		return serverName;
	}

	@Override
	public long startedAt() {
		return startedAt;
	}

	@Override
	public PServerSnapshot createSnapshot() {
		return new PServerSnapshot(id, state, type, accessLevel, taskName, onlinePlayers, startedAt,
				serverName);
	}

	@Override
	public String taskName() {
		return taskName;
	}
}
