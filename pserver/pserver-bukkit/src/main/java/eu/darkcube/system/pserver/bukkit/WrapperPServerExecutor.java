/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import de.dytanic.cloudnet.common.concurrent.ITask;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerPersistentDataStorage;
import eu.darkcube.system.pserver.common.PServerSnapshot;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.wn.*;
import eu.darkcube.system.pserver.common.packets.wn.PacketState.Response;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class WrapperPServerExecutor implements PServerExecutor {

	private final UniqueId id;
	private final PServerPersistentDataStorage storage;

	WrapperPServerExecutor(UniqueId id) {
		this.id = id;
		this.storage = new PServerStorage(id);
	}

	private static <T> CompletableFuture<T> wrap(ITask<T> task) {
		CompletableFuture<T> fut = new CompletableFuture<>();
		task.onComplete(fut::complete).onFailure(fut::completeExceptionally).onCancelled(
				t -> fut.completeExceptionally(new CancellationException("Task cancelled")));
		return fut;
	}

	@Override
	public CompletableFuture<@NotNull Boolean> start() {
		return wrap(new PacketStart(id).sendQueryAsync(PacketStart.Response.class)
				.map(PacketStart.Response::success));
	}

	@Override
	public CompletableFuture<Void> stop() {
		return wrap(new PacketStop(id).sendQueryAsync(PacketStop.Response.class).map(p -> null));
	}

	@Override
	public CompletableFuture<@NotNull Boolean> accessLevel(AccessLevel level) {
		return wrap(new PacketAccessLevelSet(id, level).sendQueryAsync(
				PacketAccessLevelSet.Response.class).map(PacketAccessLevelSet.Response::success));
	}

	@Override
	public CompletableFuture<@NotNull Boolean> addOwner(UUID uuid) {
		return wrap(new PacketAddOwner(id, uuid).sendQueryAsync(PacketAddOwner.Response.class)
				.map(PacketAddOwner.Response::success));
	}

	@Override
	public CompletableFuture<@NotNull Boolean> removeOwner(UUID uuid) {
		return wrap(new PacketRemoveOwner(id, uuid).sendQueryAsync(PacketAddOwner.Response.class)
				.map(PacketAddOwner.Response::success));
	}

	@Override
	public CompletableFuture<@NotNull PServerSnapshot> createSnapshot() {
		return wrap(new PacketCreateSnapshot(id).sendQueryAsync(PacketCreateSnapshot.Response.class)
				.map(PacketCreateSnapshot.Response::snapshot));
	}

	@Override
	public CompletableFuture<@NotNull Boolean> connectPlayer(UUID player) {
		return wrap(new PacketConnectPlayer(id, player).sendQueryAsync(
				PacketConnectPlayer.Response.class).map(PacketConnectPlayer.Response::success));
	}

	@Override
	public UniqueId id() {
		return id;
	}

	@Override
	public CompletableFuture<@NotNull State> state() {
		return wrap(new PacketState(id).sendQueryAsync(PacketState.Response.class)
				.map(Response::state));
	}

	@Override
	public CompletableFuture<@NotNull Type> type() {
		return wrap(new PacketType(id).sendQueryAsync(PacketType.Response.class)
				.map(PacketType.Response::type));
	}

	@Override
	public CompletableFuture<@NotNull AccessLevel> accessLevel() {
		return wrap(new PacketAccessLevel(id).sendQueryAsync(PacketAccessLevel.Response.class)
				.map(PacketAccessLevel.Response::accessLevel));
	}

	@Override
	public PServerPersistentDataStorage storage() {
		return storage;
	}

	@Override
	public CompletableFuture<@NotNull Long> startedAt() {
		return wrap(new PacketStartedAt(id).sendQueryAsync(PacketStartedAt.Response.class)
				.map(PacketStartedAt.Response::startedAt));
	}

	@Override
	public CompletableFuture<@NotNull Long> ontime() {
		return wrap(new PacketOntime(id).sendQueryAsync(PacketOntime.Response.class)
				.map(PacketOntime.Response::ontime));
	}

	@Override
	public CompletableFuture<@NotNull Integer> onlinePlayers() {
		return wrap(new PacketOnlinePlayers(id).sendQueryAsync(PacketOnlinePlayers.Response.class)
				.map(PacketOnlinePlayers.Response::onlinePlayers));
	}

	@Override
	public CompletableFuture<@Nullable String> serverName() {
		return wrap(new PacketServerName(id).sendQueryAsync(PacketServerName.Response.class)
				.map(PacketServerName.Response::serverName));
	}

	@Override
	public CompletableFuture<@NotNull @Unmodifiable Collection<@NotNull UUID>> owners() {
		return wrap(new PacketOwners(id).sendQueryAsync(PacketOwners.Response.class)
				.map(PacketOwners.Response::owners));
	}

	@Override
	public CompletableFuture<@Nullable String> taskName() {
		return wrap(new PacketTaskName(id).sendQueryAsync(PacketTaskName.Response.class)
				.map(PacketTaskName.Response::taskName));
	}
}
