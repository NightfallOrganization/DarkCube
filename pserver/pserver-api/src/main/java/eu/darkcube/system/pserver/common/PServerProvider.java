/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.service.ServiceTemplate;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public abstract class PServerProvider {

	private static PServerProvider instance;

	public PServerProvider() {
		instance = this;
	}

	public static @NotNull PServerProvider instance() {
		return instance;
	}

	/**
	 * @return the template prefix for pservers. This is {@code "pserver"}.
	 */
	public static @NotNull String templatePrefix() {
		return "pserver";
	}

	/**
	 * @param pserver the pserver
	 *
	 * @return a {@link ServiceTemplate} for the given pserver
	 */
	public static @NotNull ServiceTemplate template(@NotNull UniqueId pserver) {
		return new ServiceTemplate(templatePrefix(), pserver.toString(),
				ServiceTemplate.LOCAL_STORAGE);
	}

	public abstract void setPServerCommand(BiFunction<Object, String[], Boolean> command)
	throws IllegalStateException;

	/**
	 * @return true if this Server is a {@link PServerExecutor}
	 */
	public abstract @NotNull boolean isPServer();

	/**
	 * @return the current {@link PServerExecutor}, if this is a {@link PServerExecutor}. Otherwise
	 * {@link IllegalStateException}
	 *
	 * @throws IllegalStateException if this server is no a pserver
	 * @see #isPServer()
	 */
	public abstract @NotNull PServerExecutor currentPServer() throws IllegalStateException;

	/**
	 * @param pserver the id of the pserver
	 *
	 * @return the pserver with the id
	 */
	public abstract @NotNull CompletableFuture<@NotNull PServerExecutor> pServer(
			@NotNull UniqueId pserver);

	/**
	 * @param pserver the pserver
	 *
	 * @return the pserverData for the given pserver id
	 */
	public abstract @NotNull CompletableFuture<JsonDocument> pServerData(@NotNull UniqueId pserver);

	/**
	 * Sets the pserverData for a given pserver id
	 *
	 * @param pserver the pserver id
	 * @param data    the data
	 *
	 * @return whether the operation was successful
	 */
	public abstract CompletableFuture<Boolean> pServerData(UniqueId pserver, JsonDocument data);

	public abstract CompletableFuture<PServerExecutor> createPServer(
			PServerSerializable configuration);

	/**
	 * @param pserver the id of the pserver
	 *
	 * @return the pserver with the id, null if no pserver exists
	 */
	public @NotNull Optional<? extends PServerExecutor> pServerOptional(UniqueId pserver) {
		return Optional.ofNullable(pServer(pserver));
	}

	public abstract Collection<? extends PServerExecutor> getPServers();

	public abstract Collection<UniqueId> getPServerIDs(UUID owner);

	public abstract Collection<UUID> getOwners(UniqueId pserver);

	public abstract ITask<Void> delete(UniqueId pserver);

	public abstract ITask<Void> clearOwners(UniqueId id);

	public abstract ITask<Void> addOwner(UniqueId id, UUID owner);

	public abstract ITask<Void> removeOwner(UniqueId id, UUID owner);

	public abstract String newName();
}
