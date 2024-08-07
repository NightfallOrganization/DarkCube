/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.dasbabypixel.prefixplugin;

import java.util.UUID;

import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

@Api
public class ReloadSinglePrefixEvent extends ServerEvent {

    private static final HandlerList handlers = new HandlerList();

    private final @NotNull UUID uuid;
    private @NotNull String newPrefix;
    private @NotNull String newSuffix;

    @Api
    public ReloadSinglePrefixEvent(@NotNull UUID uuidOfReloadedPlayer, @NotNull String newPrefix, @NotNull String newSuffix) {
        this.uuid = uuidOfReloadedPlayer;
        this.newPrefix = newPrefix;
        this.newSuffix = newSuffix;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Api
    public UUID uuid() {
        return uuid;
    }

    @Api
    public String newPrefix() {
        return newPrefix;
    }

    @Api
    public void newPrefix(@NotNull String newPrefix) {
        this.newPrefix = newPrefix;
    }

    @Api
    public String newSuffix() {
        return newSuffix;
    }

    @Api
    public void newSuffix(@NotNull String newSuffix) {
        this.newSuffix = newSuffix;
    }

    /**
     * @deprecated use {@link #newPrefix()}
     */
    @Api
    @Deprecated
    @ScheduledForRemoval
    public String getNewPrefix() {
        return newPrefix();
    }

    /**
     * @deprecated use {@link #newPrefix(String)}
     */
    @Api
    @Deprecated
    @ScheduledForRemoval
    public void setNewPrefix(@NotNull String newPrefix) {
        newPrefix(newPrefix);
    }

    /**
     * @deprecated use {@link #newSuffix()}
     */
    @Api
    @Deprecated
    @ScheduledForRemoval
    public String getNewSuffix() {
        return newSuffix();
    }

    /**
     * @deprecated use {@link #newSuffix(String)}
     */
    @Api
    @Deprecated
    @ScheduledForRemoval
    public void setNewSuffix(@NotNull String newSuffix) {
        newSuffix(newSuffix);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
}
