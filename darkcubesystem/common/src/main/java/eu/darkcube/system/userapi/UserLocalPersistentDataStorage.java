/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.userapi.packets.PacketNWUserPersistentDataMerge;
import eu.darkcube.system.userapi.packets.PacketNWUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketNWUserPersistentDataUpdate;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class UserLocalPersistentDataStorage implements CommonPersistentDataStorage {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final UUID uniqueId;
    private volatile String name;
    private final Document.Mutable data = Document.newJsonDocument();
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();

    public UserLocalPersistentDataStorage(UUID uniqueId, String name, Document initialData) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.data.append(initialData);
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public UUID uniqueId() {
        return uniqueId;
    }

    @Override public @NotNull @UnmodifiableView PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Override public @NotNull @Unmodifiable Collection<Key> keys() {
        List<Key> keys = new ArrayList<>();
        try {
            lock.readLock().lock();
            for (String key : data.keys()) {
                keys.add(Key.fromString(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        return Collections.unmodifiableList(keys);
    }

    @Override public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        Document.Mutable d = Document.newJsonDocument();
        type.serialize(d, key.toString(), data);
        try {
            lock.writeLock().lock();
            this.data.append(d);
            cache.put(key, type.clone(data));
        } finally {
            lock.writeLock().unlock();
        }
        new PacketNWUserPersistentDataMerge(uniqueId, d).sendAsync();
        notifyNotifiers();
    }

    @Override public <T> @Nullable T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        T ret;
        try {
            lock.writeLock().lock();
            boolean contains = data.contains(key.toString());
            if (!contains) return null;
            T t = (T) cache.remove(key);
            if (t == null) {
                t = type.deserialize(data, key.toString());
            }
            data.remove(key.toString());
            ret = t;
        } finally {
            lock.writeLock().unlock();
        }
        new PacketNWUserPersistentDataRemove(uniqueId, key).sendAsync();
        notifyNotifiers();
        return ret;
    }

    @Override public <T> @Nullable T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            lock.readLock().lock();
            T t = (T) cache.get(key);
            if (t != null) return type.clone(t);
            if (!data.contains(key.toString())) return null;
            t = type.deserialize(data, key.toString());
            cache.put(key, t);
            return type.clone(t);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        T t = get(key, type);
        if (t == null) {
            t = defaultValue.get();
        }
        return t;
    }

    @Override public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        lock.writeLock().lock();
        if (!this.data.contains(key.toString())) {
            set(key, type, data);
        }
        lock.writeLock().unlock();
    }

    @Override public boolean has(@NotNull Key key) {
        try {
            lock.readLock().lock();
            return data.contains(key.toString());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override public void clear() {
        loadFromJsonDocument(Document.emptyDocument());
    }

    @Override public void loadFromJsonDocument(Document document) {
        try {
            lock.writeLock().lock();
            data.clear();
            cache.clear();
            data.append(document);
        } finally {
            lock.writeLock().unlock();
        }
        new PacketNWUserPersistentDataUpdate(uniqueId, document).sendAsync();
    }

    @Override public Document storeToJsonDocument() {
        try {
            lock.readLock().lock();
            return data.immutableCopy();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return updateNotifiers;
    }

    @Override public void clearCache() {
        try {
            lock.writeLock().lock();
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        this.updateNotifiers.add(notifier);
    }

    @Override public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        this.updateNotifiers.remove(notifier);
    }

    @Override public void remove(@NotNull Key key) {
        boolean changed = false;
        try {
            lock.writeLock().lock();
            if (data.contains(key.toString())) {
                changed = true;
                data.remove(key.toString());
                cache.remove(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
        if (changed) notifyNotifiers();
    }

    @Override public void merge(@NotNull Document data) {
        try {
            lock.writeLock().lock();
            this.data.append(data);
            this.cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override public void update(@NotNull Document document) {
        try {
            lock.writeLock().lock();
            this.data.clear();
            this.cache.clear();
            this.data.append(document);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    private void notifyNotifiers() {
        for (UpdateNotifier updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
