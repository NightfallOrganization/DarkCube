/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class LocalPersistentDataStorage implements PersistentDataStorage {
    protected final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    protected final Map<Key, Object> cache = new HashMap<>();
    protected final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    protected final Document.Mutable data = Document.newJsonDocument();

    public void appendDocument(Document document) {
        try {
            lock.writeLock().lock();
            for (String key : document.keys()) {
                cache.remove(Key.fromString(key));
            }
            data.append(document);
            notifyNotifiers();
        } finally {
            lock.writeLock().unlock();
        }
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
        try {
            lock.writeLock().lock();
            data = type.clone(data);
            if (cache.containsKey(key) && cache.get(key).equals(data)) {
                return;
            }
            cache.put(key, data);
            type.serialize(this.data, key.toString(), data);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        T ret;
        try {
            lock.writeLock().lock();
            if (!data.contains(key.toString())) {
                return null;
            }
            T old = (T) cache.remove(key);
            if (old == null) {
                old = type.deserialize(data, key.toString());
            }
            data.remove(key.toString());
            ret = type.clone(old);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return ret;
    }

    @Override public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            lock.readLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
            if (!data.contains(key.toString())) {
                return null;
            }
            T value = type.clone(type.deserialize(data, key.toString()));
            cache.put(key, value);
            return type.clone(value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
        try {
            lock.readLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
        } finally {
            lock.readLock().unlock();
        }
        T ret;
        try {
            lock.writeLock().lock();
            if (cache.containsKey(key)) {
                return type.clone((T) cache.get(key));
            }
            if (data.contains(key.toString())) {
                T value = type.clone(type.deserialize(data, key.toString()));
                cache.put(key, value);
                return type.clone(value);
            }
            T val = type.clone(defaultValue.get());
            type.serialize(data, key.toString(), val);
            cache.put(key, val);
            ret = val;
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return ret;
    }

    @Override public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        try {
            lock.readLock().lock();
            if (this.data.contains(key.toString())) {
                return;
            }
        } finally {
            lock.readLock().unlock();
        }
        try {
            lock.writeLock().lock();
            if (this.data.contains(key.toString())) {
                return;
            }
            data = type.clone(data);
            type.serialize(this.data, key.toString(), data);
            cache.put(key, data);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
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
        try {
            lock.writeLock().lock();
            data.clear();
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
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

    @Override public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return Collections.unmodifiableCollection(updateNotifiers);
    }

    @Override public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.add(notifier);
    }

    @Override public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.remove(notifier);
    }

    @Override public void clearCache() {
        try {
            lock.writeLock().lock();
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected void notifyNotifiers() {
        for (UpdateNotifier updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
