/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public abstract class CommonUserAPI implements UserAPI {
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();
    protected final List<UserModifier> modifiers = new CopyOnWriteArrayList<>();
    protected final LoadingCache<UUID, CommonUser> userCache;
    protected final CommonRemoteUserPacketHandler packetHandler = new CommonRemoteUserPacketHandler(this);

    public CommonUserAPI() {
        userCache = Caffeine.newBuilder().softValues().removalListener(new UserCacheRemovalListener()).build(uniqueId -> {
            CommonUser user = loadUser(uniqueId);
            for (UserModifier modifier : modifiers) {
                modifier.onLoad(user);
            }
            return user;
        });
        UserAPIHolder.instance(this);
        packetHandler.registerHandlers();
    }

    @Override public @NotNull CommonUser user(UUID uniqueId) {
        return userCache.get(uniqueId);
    }

    @Override public final void addModifier(UserModifier modifier) {
        try {
            lock.writeLock().lock();
            modifiers.add(modifier);
            loadedUsersForEach(modifier::onLoad);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override public final void removeModifier(UserModifier modifier) {
        try {
            lock.writeLock().lock();
            loadedUsersForEach(modifier::onUnload);
            modifiers.remove(modifier);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected abstract CommonUser loadUser(UUID uniqueId);

    protected void loadedUsersForEach(Consumer<CommonUser> consumer) {
        try {
            lock.readLock().lock();
            for (CommonUser user : userCache.asMap().values()) {
                consumer.accept(user);
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    private void userUnloaded(@NotNull CommonUser user) {
        try {
            lock.writeLock().lock();
            for (UserModifier modifier : modifiers) {
                modifier.onUnload(user);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void userCollected(@NotNull UUID ignoredUniqueId) {
        // todo do we really want to enable use of UserModifier#onUnload(UserData)
    }

    protected class UserCacheRemovalListener implements RemovalListener<UUID, CommonUser> {

        @Override public void onRemoval(@Nullable UUID uniqueId, @Nullable CommonUser user, @NotNull RemovalCause cause) {
            if (uniqueId == null) throw new AssertionError("User UniqueID was garbage collected");
            switch (cause) {
                case REPLACED -> throw new AssertionError("Replacing means that we have multiple instances for the same User");
                case EXPIRED -> throw new AssertionError("Expiry is not enabled");
                case SIZE -> throw new AssertionError("A maximum size is not configured");
            }
            if (user != null) {
                userUnloaded(user);
            } else {
                userCollected(uniqueId);
            }
        }
    }
}
