/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.bukkit;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.pserver.common.PServerExecutor;
import eu.darkcube.system.pserver.common.PServerSnapshot;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.wn.PacketAccessLevel;
import eu.darkcube.system.pserver.common.packets.wn.PacketAccessLevelSet;
import eu.darkcube.system.pserver.common.packets.wn.PacketAddOwner;
import eu.darkcube.system.pserver.common.packets.wn.PacketConnectPlayer;
import eu.darkcube.system.pserver.common.packets.wn.PacketCreateSnapshot;
import eu.darkcube.system.pserver.common.packets.wn.PacketOnlinePlayers;
import eu.darkcube.system.pserver.common.packets.wn.PacketOntime;
import eu.darkcube.system.pserver.common.packets.wn.PacketOwners;
import eu.darkcube.system.pserver.common.packets.wn.PacketRemoveOwner;
import eu.darkcube.system.pserver.common.packets.wn.PacketServerName;
import eu.darkcube.system.pserver.common.packets.wn.PacketStart;
import eu.darkcube.system.pserver.common.packets.wn.PacketStartedAt;
import eu.darkcube.system.pserver.common.packets.wn.PacketState;
import eu.darkcube.system.pserver.common.packets.wn.PacketState.Response;
import eu.darkcube.system.pserver.common.packets.wn.PacketStop;
import eu.darkcube.system.pserver.common.packets.wn.PacketTaskName;
import eu.darkcube.system.pserver.common.packets.wn.PacketType;
import eu.darkcube.system.util.data.CustomPersistentDataProvider;
import eu.darkcube.system.util.data.PersistentDataStorage;

public class WrapperPServerExecutor implements PServerExecutor {

    private final UniqueId id;
    private final PersistentDataStorage storage;

    WrapperPServerExecutor(UniqueId id) {
        this.id = id;
        this.storage = CustomPersistentDataProvider.dataProvider().persistentData("pserver_data", Key.key("", id.toString()));
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> start() {
        return new PacketStart(id).sendQueryAsync(PacketStart.Response.class).thenApply(PacketStart.Response::success);
    }

    @Override
    public @NotNull CompletableFuture<Void> stop() {
        return new PacketStop(id).sendQueryAsync(PacketStop.Response.class).thenApply(p -> null);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> accessLevel(@NotNull AccessLevel level) {
        return new PacketAccessLevelSet(id, level).sendQueryAsync(PacketAccessLevelSet.Response.class).thenApply(PacketAccessLevelSet.Response::success);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> addOwner(UUID uuid) {
        return new PacketAddOwner(id, uuid).sendQueryAsync(PacketAddOwner.Response.class).thenApply(PacketAddOwner.Response::success);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> removeOwner(UUID uuid) {
        return new PacketRemoveOwner(id, uuid).sendQueryAsync(PacketRemoveOwner.Response.class).thenApply(PacketRemoveOwner.Response::success);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull PServerSnapshot> createSnapshot() {
        return new PacketCreateSnapshot(id).sendQueryAsync(PacketCreateSnapshot.Response.class).thenApply(PacketCreateSnapshot.Response::snapshot);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Boolean> connectPlayer(UUID player) {
        return new PacketConnectPlayer(id, player).sendQueryAsync(PacketConnectPlayer.Response.class).thenApply(PacketConnectPlayer.Response::success);
    }

    @Override
    public @NotNull UniqueId id() {
        return id;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull State> state() {
        return new PacketState(id).sendQueryAsync(PacketState.Response.class).thenApply(Response::state);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Type> type() {
        return new PacketType(id).sendQueryAsync(PacketType.Response.class).thenApply(PacketType.Response::type);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull AccessLevel> accessLevel() {
        return new PacketAccessLevel(id).sendQueryAsync(PacketAccessLevel.Response.class).thenApply(PacketAccessLevel.Response::accessLevel);
    }

    @Override
    public PersistentDataStorage storage() {
        return storage;
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Long> startedAt() {
        return new PacketStartedAt(id).sendQueryAsync(PacketStartedAt.Response.class).thenApply(PacketStartedAt.Response::startedAt);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Long> ontime() {
        return new PacketOntime(id).sendQueryAsync(PacketOntime.Response.class).thenApply(PacketOntime.Response::ontime);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull Integer> onlinePlayers() {
        return new PacketOnlinePlayers(id).sendQueryAsync(PacketOnlinePlayers.Response.class).thenApply(PacketOnlinePlayers.Response::onlinePlayers);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable String> serverName() {
        return new PacketServerName(id).sendQueryAsync(PacketServerName.Response.class).thenApply(PacketServerName.Response::serverName);
    }

    @Override
    public @NotNull CompletableFuture<@NotNull @Unmodifiable Collection<@NotNull UUID>> owners() {
        return new PacketOwners(id).sendQueryAsync(PacketOwners.Response.class).thenApply(PacketOwners.Response::owners);
    }

    @Override
    public @NotNull CompletableFuture<@Nullable String> taskName() {
        return new PacketTaskName(id).sendQueryAsync(PacketTaskName.Response.class).thenApply(PacketTaskName.Response::taskName);
    }
}
