/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PServerExecutor {

	String CHANNEL = "pserver";

	/**
	 * @return the current state of the pserver
	 */
	@NotNull State state();

	/**
	 * Starts this pserver.<br> The future contains whether the start was successful
	 *
	 * @return a future
	 */
	CompletableFuture<Boolean> start();

	/**
	 * Stops the pserver.
	 *
	 * @return a future
	 */
	@NotNull CompletableFuture<Void> stop();

	/**
	 * @return the amount of players currently playing on the pserver
	 */
	int onlinePlayers();

	/**
	 * @return the pserver type
	 */
	@NotNull Type type();

	/**
	 * @return the access level of this pserver
	 */
	@NotNull AccessLevel accessLevel();

	/**
	 * Sets the access level
	 *
	 * @param level the new level
	 */
	CompletableFuture<Boolean> publicAccess(@NotNull AccessLevel level);

	/**
	 * @return the unique id of this pserver
	 */
	UniqueId id();

	/**
	 * @return the amount of time (in milliseconds) how long this pserver has been online
	 */
	long ontime();

	/**
	 * @return an unmodifiable collection of the owners of this pserver
	 */
	@UnmodifiableView Collection<@NotNull UUID> owners();

	/**
	 * @return the name of this pserver. This might be auto-generated. Two pservers with the same
	 * name may not exist at the same time.
	 */
	@Nullable String serverName();

	/**
	 * @return when this pserver was started
	 */
	long startedAt();

	/**
	 * @return a new {@link PServerSnapshot} for this pserver
	 */
	@NotNull PServerSnapshot createSnapshot();

	/**
	 * @return the taskname of this pserver
	 */
	String taskName();

	/**
	 * Connects a player to the pserver
	 *
	 * @param player the uuid of the player
	 *
	 * @return a future for when the player is connected. The future contains false when connection
	 * failed
	 */
	CompletableFuture<Boolean> connectPlayer(UUID player);

	enum State {
		RUNNING, STARTING, STOPPING, OFFLINE
	}

	enum Type {
		WORLD, GAMEMODE
	}

	enum AccessLevel {
		PRIVATE, PUBLIC
	}
}
