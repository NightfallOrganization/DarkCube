/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PServerExecutor {

    /**
     * Starts this pserver.<br> The future contains whether the start was successful
     *
     * @return a future
     */
    @NotNull CompletableFuture<@NotNull Boolean> start();

    /**
     * Stops the pserver.
     *
     * @return a future
     */
    @NotNull CompletableFuture<Void> stop();

    /**
     * Sets the access level
     *
     * @param level the new level
     */
    @NotNull CompletableFuture<@NotNull Boolean> accessLevel(@NotNull AccessLevel level);

    /**
     * Adds an owner to this pserver
     *
     * @param uuid the owner
     * @return true if successful
     */
    @NotNull CompletableFuture<@NotNull Boolean> addOwner(UUID uuid);

    /**
     * Adds an owner to this pserver
     *
     * @param uuid the owner
     * @return true if successful
     */
    @NotNull CompletableFuture<@NotNull Boolean> removeOwner(UUID uuid);

    /**
     * @return a new {@link PServerSnapshot} for this pserver
     */
    @NotNull CompletableFuture<@NotNull PServerSnapshot> createSnapshot();

    /**
     * Connects a player to the pserver
     *
     * @param player the uuid of the player
     * @return a future for when the player is connected. The future contains false when connection
     * failed
     */
    @NotNull CompletableFuture<@NotNull Boolean> connectPlayer(UUID player);

    /**
     * @return the unique id of this pserver
     */
    @NotNull UniqueId id();

    /**
     * @return the current state of the pserver
     */
    @NotNull CompletableFuture<@NotNull State> state();

    /**
     * @return the pserver type
     */
    @NotNull CompletableFuture<@NotNull Type> type();

    /**
     * @return the access level of this pserver
     */
    @NotNull CompletableFuture<@NotNull AccessLevel> accessLevel();

    /**
     * @return a {@link PServerPersistentDataStorage} for this pserver
     */
    PServerPersistentDataStorage storage();

    /**
     * @return when this pserver was started
     */
    @NotNull CompletableFuture<@NotNull Long> startedAt();

    /**
     * @return the amount of time (in milliseconds) how long this pserver has been online
     */
    @NotNull CompletableFuture<@NotNull Long> ontime();

    /**
     * @return the amount of players currently playing on the pserver
     */
    @NotNull CompletableFuture<@NotNull Integer> onlinePlayers();

    /**
     * @return the name of this pserver. This might be auto-generated. Two pservers with the same
     * name may not exist at the same time.
     */
    @NotNull CompletableFuture<@Nullable String> serverName();

    /**
     * @return an unmodifiable collection of the owners of this pserver
     */
    @NotNull CompletableFuture<@NotNull @Unmodifiable Collection<@NotNull UUID>> owners();

    /**
     * @return the taskname of this pserver
     */
    @Deprecated @ApiStatus.Internal @NotNull CompletableFuture<@Nullable String> taskName();

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
