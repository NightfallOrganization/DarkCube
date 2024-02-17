/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.impl.common.userapi;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.userapi.packets.PacketWNUserPersistentDataLoad;
import eu.darkcube.system.userapi.packets.PacketWNUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketWNUserPersistentDataSet;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class CommonUserRemotePersistentDataStorage implements CommonPersistentDataStorage {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final UUID uniqueId;
    private final Map<Key, Object> caches = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private Document.Mutable data = Document.newJsonDocument();

    public CommonUserRemotePersistentDataStorage(UUID uniqueId, Document initialData) {
        this.uniqueId = uniqueId;
        this.data.append(initialData);
    }

    @Override public @UnmodifiableView @NotNull PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Override public @NotNull Collection<Key> keys() {
        List<Key> keys = new ArrayList<>();
        try {
            lock.readLock().lock();
            for (String s : data.keys()) {
                keys.add(Key.fromString(s));
            }
        } finally {
            lock.readLock().unlock();
        }
        return Collections.unmodifiableCollection(keys);
    }

    @Override public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        Document.Mutable d = Document.newJsonDocument();
        type.serialize(d, key.toString(), data);
        try {
            lock.writeLock().lock();
            this.data.append(d);
            caches.put(key, data);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        new PacketWNUserPersistentDataSet(uniqueId, d).sendAsync();
    }

    @Override public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        T ret;
        try {
            lock.writeLock().lock();
            boolean contains = data.contains(key.toString());
            if (!contains) return null;
            T t = (T) caches.remove(key);
            if (t == null) {
                t = type.deserialize(this.data, key.toString());
            }
            data.remove(key.toString());
            ret = t;
        } finally {
            lock.writeLock().unlock();
        }
        new PacketWNUserPersistentDataRemove(uniqueId, key).sendAsync();
        notifyNotifiers();
        return ret;
    }

    @Override public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            lock.readLock().lock();
            T t = (T) caches.get(key);
            if (t != null) return t;
            if (!data.contains(key.toString())) return null;
            t = type.deserialize(data, key.toString());
            caches.put(key, t);
            return t;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
        T t;
        try {
            lock.readLock().lock();
            t = get(key, type);
        } finally {
            lock.readLock().unlock();
        }
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
        loadFromJsonDocument(Document.newJsonDocument());
    }

    @Override public void loadFromJsonDocument(Document document) {
        try {
            lock.writeLock().lock();
            data.clear();
            caches.clear();
            data.append(document);
            new PacketWNUserPersistentDataLoad(uniqueId, document.immutableCopy()).send();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override public Document storeToJsonDocument() {
        try {
            lock.readLock().lock();
            return data.immutableCopy();
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override public void clearCache() {
        try {
            lock.writeLock().lock();
            caches.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return Collections.unmodifiableCollection(updateNotifiers);
    }

    @Override public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.add(notifier);
    }

    @Override public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.remove(notifier);
    }

    @Override public void remove(@NotNull Key key) {
        boolean changed = false;
        try {
            lock.writeLock().lock();
            if (data.contains(key.toString())) {
                changed = true;
                data.remove(key.toString());
                caches.remove(key);
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
            this.caches.clear();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override public void update(@NotNull Document document) {
        try {
            lock.writeLock().lock();
            this.data.clear();
            this.caches.clear();
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
