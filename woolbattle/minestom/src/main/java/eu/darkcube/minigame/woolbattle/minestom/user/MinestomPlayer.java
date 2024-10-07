/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.user;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.EntityImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.kyori.adventure.text.Component;
import net.minestom.server.ServerFlag;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.network.packet.server.CachedPacket;
import net.minestom.server.network.packet.server.SendablePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.thread.Acquirable;
import net.minestom.server.utils.time.Cooldown;

public class MinestomPlayer extends Player implements EntityImpl {
    private static final VarHandle LATENCY;
    private static final VarHandle DISPLAY_NAME;
    private static boolean isIn_UNSAFE_init;
    private static boolean isIn_remove;
    private static boolean isIn_setGameMode;
    private static boolean joining = true;

    public MinestomPlayer(@NotNull UUID uuid, @NotNull String username, @NotNull PlayerConnection playerConnection) {
        super(uuid, username, playerConnection);
        this.itemPickupCooldown = new Cooldown(Duration.ZERO);
    }

    @Override
    public void sendPacket(@NotNull SendablePacket packet) {
        if (isIn_UNSAFE_init || isIn_remove || isIn_setGameMode) {
            switch (packet) {
                case PlayerInfoUpdatePacket _, PlayerInfoRemovePacket _ -> {
                }
                case CachedPacket c -> {
                    var p = c.packet(playerConnection.getConnectionState());
                    if (p instanceof PlayerInfoUpdatePacket || p instanceof PlayerInfoRemovePacket) {
                    } else {
                        super.sendPacket(c);
                    }
                }
                default -> super.sendPacket(packet);
            }
        } else {
            super.sendPacket(packet);
        }
    }

    @Override
    public CompletableFuture<Void> UNSAFE_init() {
        isIn_UNSAFE_init = true;
        var fut = super.UNSAFE_init();
        isIn_UNSAFE_init = false;
        return fut;
    }

    @Override
    public void remove(boolean permanent) {
        isIn_remove = true;
        super.remove(permanent);
        isIn_remove = false;
    }

    @Override
    public void refreshLatency(int latency) {
        LATENCY.setVolatile(this, latency);
    }

    @Override
    public void setDisplayName(@Nullable Component displayName) {
        DISPLAY_NAME.set(this, displayName);
    }

    @Override
    public boolean setGameMode(@NotNull GameMode gameMode) {
        isIn_setGameMode = true;
        var r = super.setGameMode(gameMode);
        isIn_setGameMode = false;
        return r;
    }

    public void sendPacketDirect(@NotNull SendablePacket packet) {
        super.sendPacket(packet);
    }

    @Override
    public @NotNull PlayerInfoUpdatePacket getAddPlayerToList() {
        return super.getAddPlayerToList();
    }

    @Override
    public @NotNull PlayerInfoRemovePacket getRemovePlayerToList() {
        return super.getRemovePlayerToList();
    }

    static {
        try {
            var lookup = MethodHandles.privateLookupIn(Player.class, MethodHandles.lookup());
            LATENCY = lookup.findVarHandle(Player.class, "latency", int.class);
            DISPLAY_NAME = lookup.findVarHandle(Player.class, "displayName", Component.class);
        } catch (Throwable t) {
            throw new Error(t);
        }
    }

    private Entity spectating;

    @Override
    public void spectate(@NotNull Entity entity) {
        this.spectating = entity;
        super.spectate(entity);
    }

    public void postJoin() {
        joining = false;
        this.instance.getEntityTracker().nearbyEntitiesByChunkRange(position, ServerFlag.ENTITY_VIEW_DISTANCE, EntityTracker.Target.PLAYERS, newEntity -> {
            if (newEntity == this) return;
            if (newEntity instanceof MinestomPlayer player) {
                player.trackingUpdate.add(this);
            }
            this.trackingUpdate.add(newEntity);
        });
    }

    @Override
    public boolean isPreventBlockPlacement() {
        var user = this.user;
        if (user != null) {
            var team = user.team();
            if (team != null && team.spectator()) {
                return true;
            }
        }
        return super.isPreventBlockPlacement();
    }

    // @Override
    // public boolean isAutoViewable() {
    //     return !joining;
    // }

    @Nullable
    public Entity spectating() {
        return spectating;
    }

    public Entity camera() {
        return spectating == null ? this : spectating;
    }

    public @Nullable CommonWBUser user() {
        return user;
    }

    public void user(@Nullable CommonWBUser user) {
        this.user = user;
    }

    private @Nullable CommonWBUser user;

    @Override
    public @NotNull CommonWBUser handle() {
        return Objects.requireNonNull(user);
    }

    @Override
    public void handle(eu.darkcube.minigame.woolbattle.api.entity.Entity handle) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull Acquirable<? extends MinestomPlayer> acquirable() {
        return (Acquirable<? extends MinestomPlayer>) super.acquirable();
    }
}
