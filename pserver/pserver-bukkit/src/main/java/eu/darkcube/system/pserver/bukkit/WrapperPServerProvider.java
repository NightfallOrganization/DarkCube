/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.pserver.common.*;
import eu.darkcube.system.pserver.common.packets.wn.*;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class WrapperPServerProvider extends PServerProvider {

    private static final WrapperPServerProvider instance = new WrapperPServerProvider();

    volatile WrapperPServerExecutor self;

    private WrapperPServerProvider() {
    }

    public static @NotNull WrapperPServerProvider instance() {
        return instance;
    }

    public static void init() {
    }

    @Override public void setPServerCommand(BiFunction<Object, String[], Boolean> command) throws IllegalStateException {
        PServerWrapper.setPServerCommand(command::apply);
    }

    @Override public boolean isPServer() {
        return self != null;
    }

    @Override public @NotNull PServerExecutor currentPServer() throws IllegalStateException {
        if (self == null) throw new IllegalStateException();
        return self;
    }

    @Override public @NotNull CompletableFuture<@NotNull Collection<@NotNull UniqueId>> registeredPServers() {
        return new PacketRegisteredPServers()
                .sendQueryAsync(PacketRegisteredPServers.Response.class)
                .thenApply(PacketRegisteredPServers.Response::ids);
    }

    @Override public @NotNull CompletableFuture<@Nullable WrapperPServerExecutor> pserver(@NotNull UniqueId pserver) {
        return CompletableFuture.completedFuture(new WrapperPServerExecutor(pserver));
    }

    @Override public @NotNull CompletableFuture<@NotNull Boolean> pserverExists(@NotNull UniqueId pserver) {
        return new PacketExists(pserver).sendQueryAsync(PacketExists.Response.class).thenApply(PacketExists.Response::exists);
    }

    @Override public CompletableFuture<WrapperPServerExecutor> createPServer(PServerBuilder builder) {
        return new PacketCreate(builder.clone())
                .sendQueryAsync(PacketCreate.Response.class)
                .thenApply(PacketCreate.Response::snapshot)
                .thenApply(PServerSnapshot::uniqueId)
                .thenCompose(this::pserver);
    }

    @Override public CompletableFuture<Collection<? extends PServerExecutor>> pservers() {
        return new PacketPServers()
                .sendQueryAsync(PacketPServers.Response.class)
                .thenApply(PacketPServers.Response::snapshots)
                .thenApply(l -> l.stream().map(s -> {
                    try {
                        return this.pserver(s.uniqueId()).get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()))
                .thenApply(Collections::unmodifiableCollection);
    }

    @Override public CompletableFuture<Collection<UniqueId>> pservers(UUID owner) {
        return new PacketPServersByOwner(owner)
                .sendQueryAsync(PacketPServersByOwner.Response.class)
                .thenApply(PacketPServersByOwner.Response::ids)
                .thenApply(Collections::unmodifiableCollection);
    }

}
