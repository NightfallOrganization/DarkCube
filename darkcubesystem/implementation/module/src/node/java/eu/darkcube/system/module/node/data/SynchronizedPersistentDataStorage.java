/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.node.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataClearSet;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataRemove;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataSet;

/**
 * A data storage that is synchronized over the entire cloud system
 */
public class SynchronizedPersistentDataStorage implements PersistentDataStorage {
    private final Key key;
    private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
    private final Document.Mutable data = Document.newJsonDocument();
    private final Map<Key, Object> cache = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private final AtomicBoolean saving = new AtomicBoolean(false);
    private final AtomicBoolean saveAgain = new AtomicBoolean(false);

    SynchronizedPersistentDataStorage(Key key) {
        this.key = key;
    }

    @Override
    public @UnmodifiableView @NotNull PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Override
    public @NotNull Collection<Key> keys() {
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

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        try {
            lock.writeLock().lock();
            data = type.clone(data);
            if (cache.containsKey(key) && cache.get(key).equals(data)) {
                return;
            }
            cache.put(key, data);
            Document.Mutable d = Document.newJsonDocument();
            type.serialize(d, key.toString(), data);
            this.data.append(d);
            new PacketNodeWrapperDataSet(this.key, d).sendAsync();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public <T> T remove(@NotNull Key key, @Nullable PersistentDataType<T> type) {
        T ret;
        try {
            lock.writeLock().lock();
            if (!data.contains(key.toString())) {
                return null;
            }
            T old = (T) cache.remove(key);
            if (old == null && type != null) {
                old = type.deserialize(data, key.toString());
            }
            data.remove(key.toString());
            new PacketNodeWrapperDataRemove(this.key, key).sendAsync();
            ret = type != null ? type.clone(old) : null;
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return ret;
    }

    @Override
    public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
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

    @Override
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
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
            Document.Mutable d = Document.newJsonDocument();
            type.serialize(d, key.toString(), val);
            this.data.append(d);
            new PacketNodeWrapperDataSet(this.key, d).sendAsync();
            cache.put(key, val);
            ret = type.clone(val);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
        return ret;
    }

    @Override
    public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
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
            Document.Mutable d = Document.newJsonDocument();
            type.serialize(d, key.toString(), data);
            this.data.append(d);
            new PacketNodeWrapperDataSet(this.key, d).sendAsync();
            cache.put(key, data);
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public boolean has(@NotNull Key key) {
        try {
            lock.readLock().lock();
            return data.contains(key.toString());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        try {
            lock.writeLock().lock();
            clearData();
            new PacketNodeWrapperDataClearSet(key, Document.newJsonDocument()).sendAsync();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public void loadFromJsonDocument(Document document) {
        try {
            lock.writeLock().lock();
            clearData();
            data.append(document);
            new PacketNodeWrapperDataClearSet(key, data).sendAsync();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    public void append(Document document) {
        try {
            lock.writeLock().lock();
            data.append(document);
            new PacketNodeWrapperDataSet(key, document).sendAsync();
        } finally {
            lock.writeLock().unlock();
        }
        notifyNotifiers();
    }

    @Override
    public Document storeToJsonDocument() {
        try {
            lock.readLock().lock();
            return Document.newJsonDocument().append(data);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return Collections.unmodifiableCollection(updateNotifiers);
    }

    @Override
    public void clearCache() {
        try {
            lock.writeLock().lock();
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.add(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        updateNotifiers.remove(notifier);
    }

    private void save() {
        if (saving.compareAndSet(false, true)) {
            Task<Boolean> fut = SynchronizedPersistentDataStorages.database.insertAsync(key.toString(), storeToJsonDocument());
            fut.thenAccept(success -> {
                if (success) {
                    saving.set(false);
                    if (saveAgain.compareAndSet(true, false)) {
                        save();
                    }
                } else {
                    System.out.println("Failed to save - trying again");
                    save();
                }
            });
            fut.exceptionally(t -> {
                saving.set(false);
                t.printStackTrace();
                return null;
            });
            //            .addListener(new ITaskListener<Boolean>() {
            //                @Override public void onComplete(ITask<Boolean> task, Boolean aBoolean) {
            //                    if (aBoolean) {
            //                        saveAgain.set(false);
            //                        SynchronizedPersistentDataStorages.database
            //                                .updateAsync(key.toString(), storeToJsonDocument())
            //                                .addListener(new ITaskListener<Boolean>() {
            //                                    @Override public void onComplete(ITask<Boolean> task, Boolean aBoolean) {
            //                                        saving.set(false);
            //                                        if (saveAgain.compareAndSet(true, false)) {
            //                                            save();
            //                                        }
            //                                    }
            //
            //                                    @Override public void onCancelled(ITask<Boolean> task) {
            //                                        new Error("Task cancelled").printStackTrace();
            //                                    }
            //
            //                                    @Override public void onFailure(ITask<Boolean> task, Throwable th) {
            //                                        th.printStackTrace();
            //                                        saving.set(false);
            //                                        save();
            //                                    }
            //                                });
            //                    } else {
            //                        saveAgain.set(false);
            //                        SynchronizedPersistentDataStorages.database
            //                                .insertAsync(key.toString(), storeToJsonDocument())
            //                                .addListener(new ITaskListener<Boolean>() {
            //                                    @Override public void onComplete(ITask<Boolean> task, Boolean aBoolean) {
            //                                        saving.set(false);
            //                                        if (saveAgain.compareAndSet(true, false)) {
            //                                            save();
            //                                        }
            //                                    }
            //
            //                                    @Override public void onCancelled(ITask<Boolean> task) {
            //                                        new Error("Task cancelled").printStackTrace();
            //                                    }
            //
            //                                    @Override public void onFailure(ITask<Boolean> task, Throwable th) {
            //                                        th.printStackTrace();
            //                                        saving.set(false);
            //                                        save();
            //                                    }
            //                                });
            //                    }
            //                }
            //
            //                @Override public void onCancelled(ITask<Boolean> task) {
            //                    new Error("Task cancelled").printStackTrace();
            //                }
            //
            //                @Override public void onFailure(ITask<Boolean> task, Throwable th) {
            //                    th.printStackTrace();
            //                    saving.set(false);
            //                    save();
            //                }
            //            });
        } else {
            saveAgain.set(true);
            if (!saving.get()) {
                save();
            }
        }
    }

    private void clearData() {
        cache.clear();
        for (String s : new ArrayList<>(data.keys())) data.remove(s);
    }

    private void notifyNotifiers() {
        save(); // Do this here cuz were lazy
        for (UpdateNotifier updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }

    public Key key() {
        return key;
    }
}
