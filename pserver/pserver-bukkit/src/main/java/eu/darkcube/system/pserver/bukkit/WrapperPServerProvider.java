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
import eu.darkcube.system.pserver.common.*;
import eu.darkcube.system.pserver.common.packets.wn.PacketCreate;
import eu.darkcube.system.pserver.common.packets.wn.PacketExists;
import eu.darkcube.system.pserver.common.packets.wn.PacketExists.Response;
import eu.darkcube.system.pserver.common.packets.wn.PacketPServers;
import eu.darkcube.system.pserver.common.packets.wn.PacketPServersByOwner;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class WrapperPServerProvider extends PServerProvider {

	private static final WrapperPServerProvider instance = new WrapperPServerProvider();

	volatile WrapperPServerExecutor self;

	private WrapperPServerProvider() {
	}

	public static WrapperPServerProvider instance() {
		return instance;
	}

	public static void init() {
	}

	private static <T> CompletableFuture<T> wrap(ITask<T> task) {
		CompletableFuture<T> fut = new CompletableFuture<>();
		task.onComplete(fut::complete).onFailure(fut::completeExceptionally).onCancelled(
				t -> fut.completeExceptionally(new CancellationException("Task cancelled!")));
		return fut;
	}

	@Override
	public void setPServerCommand(BiFunction<Object, String[], Boolean> command)
	throws IllegalStateException {
		PServerWrapper.setPServerCommand(command::apply);
	}

	@Override
	public boolean isPServer() {
		return self != null;
	}

	@Override
	public PServerExecutor currentPServer() throws IllegalStateException {
		if (self == null)
			throw new IllegalStateException();
		return self;
	}

	@Override
	public CompletableFuture<@Nullable WrapperPServerExecutor> pserver(UniqueId pserver) {
		return CompletableFuture.completedFuture(new WrapperPServerExecutor(pserver));
	}

	@Override
	public CompletableFuture<@NotNull Boolean> pserverExists(UniqueId pserver) {
		return wrap(new PacketExists(pserver).sendQueryAsync(PacketExists.Response.class)
				.map(Response::exists));
	}

	@Override
	public CompletableFuture<WrapperPServerExecutor> createPServer(PServerBuilder builder) {
		return wrap(new PacketCreate(builder.clone()).sendQueryAsync(PacketCreate.Response.class)
				.map(PacketCreate.Response::snapshot).map(PServerSnapshot::uniqueId)
				.map(this::pserver).map(f -> {
					try {
						return f.get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				}));
	}

	@Override
	public CompletableFuture<Collection<? extends PServerExecutor>> pservers() {
		return wrap(new PacketPServers().sendQueryAsync(PacketPServers.Response.class)
				.map(PacketPServers.Response::snapshots).map(l -> l.stream().map(s -> {
					try {
						return this.pserver(s.uniqueId()).get();
					} catch (InterruptedException | ExecutionException e) {
						throw new RuntimeException(e);
					}
				}).collect(Collectors.toList())).map(Collections::unmodifiableCollection));
	}

	@Override
	public CompletableFuture<Collection<UniqueId>> pservers(UUID owner) {
		return wrap(new PacketPServersByOwner(owner).sendQueryAsync(
						PacketPServersByOwner.Response.class).map(PacketPServersByOwner.Response::ids)
				.map(Collections::unmodifiableCollection));
	}

}
