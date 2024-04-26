/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.cloudnetservice.driver.service.ServiceTemplate;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import java.util.Collection;
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
     * @return a {@link ServiceTemplate} for the given pserver
     */
    public static @NotNull ServiceTemplate template(@NotNull UniqueId pserver) {
        return ServiceTemplate.builder().storage(ServiceTemplate.LOCAL_STORAGE).prefix(templatePrefix()).name(pserver.toString()).build();
    }

    public abstract void setPServerCommand(BiFunction<Object, String[], Boolean> command) throws IllegalStateException;

    /**
     * @return true if this Server is a {@link PServerExecutor}
     */
    public abstract boolean isPServer();

    /**
     * @return the current {@link PServerExecutor}, if this is a {@link PServerExecutor}. Otherwise
     * throws {@link IllegalStateException}
     * @throws IllegalStateException if this server is no a pserver
     * @see #isPServer()
     */
    public abstract @NotNull PServerExecutor currentPServer() throws IllegalStateException;

    public abstract @NotNull CompletableFuture<@NotNull Collection<@NotNull UniqueId>> registeredPServers();

    /**
     * This will get a {@link PServerExecutor} instance.
     *
     * @param pserver the id of the pserver
     * @return the pserver with the id
     */
    public abstract @NotNull CompletableFuture<@Nullable ? extends PServerExecutor> pserver(@NotNull UniqueId pserver);

    /**
     * @param pserver the pserver id
     * @return whether the pserver is loaded
     */
    public abstract @NotNull CompletableFuture<@NotNull Boolean> pserverExists(@NotNull UniqueId pserver);

    public abstract CompletableFuture<? extends PServerExecutor> createPServer(PServerBuilder builder);

    /**
     * @return all running pservers
     */
    public abstract CompletableFuture<Collection<? extends PServerExecutor>> pservers();

    /**
     * @param owner a player
     * @return all pservers the player is owner of
     */
    public abstract CompletableFuture<Collection<UniqueId>> pservers(UUID owner);

}
