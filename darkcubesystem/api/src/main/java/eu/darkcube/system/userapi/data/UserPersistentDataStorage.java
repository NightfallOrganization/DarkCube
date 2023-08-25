/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.userapi.data;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.userapi.BukkitUser;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataSet;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class UserPersistentDataStorage implements PersistentDataStorage {

    private final BukkitUser user;
    private final Map<Key, Object> caches = new HashMap<>();
    private final Collection<@NotNull UpdateNotifier> updateNotifiers = new CopyOnWriteArrayList<>();
    private Document.Mutable data = Document.newJsonDocument();

    public UserPersistentDataStorage(BukkitUser user) {
        this.user = user;
    }

    @Override public @UnmodifiableView @NotNull PersistentDataStorage unmodifiable() {
        return new UnmodifiablePersistentDataStorage(this);
    }

    @Override public @NotNull Collection<Key> keys() {
        List<Key> keys = new ArrayList<>();
        try {
            user.lock();
            for (String s : data.keys()) {
                keys.add(Key.fromString(s));
            }
        } finally {
            user.unlock();
        }
        return Collections.unmodifiableCollection(keys);
    }

    @Override public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        Document.Mutable d = Document.newJsonDocument();
        type.serialize(d, key.toString(), data);
        try {
            user.lock();
            this.data.append(d);
            caches.put(key, data);
        } finally {
            user.unlock();
        }
        notifyNotifiers();
        new PacketUserPersistentDataSet(user.getUniqueId(), d).sendAsync();
    }

    public void remove(Key key) {
        boolean changed = false;
        try {
            user.lock();
            if (data.contains(key.toString())) {
                changed = true;
                data.remove(key.toString());
                caches.remove(key);
            }
        } finally {
            user.unlock();
        }
        if (changed) notifyNotifiers();
    }

    @Override public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        T ret;
        try {
            user.lock();
            boolean contains = data.contains(key.toString());
            if (!contains) return null;
            T t = (T) caches.remove(key);
            if (t == null) {
                t = type.deserialize(this.data, key.toString());
            }
            data.remove(key.toString());
            ret = t;
        } finally {
            user.unlock();
        }
        new PacketUserPersistentDataRemove(user.getUniqueId(), key).sendAsync();
        notifyNotifiers();
        return ret;
    }

    @Override public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        try {
            user.lock();
            T t = (T) caches.get(key);
            if (t != null) return t;
            if (!data.contains(key.toString())) return null;
            t = type.deserialize(data, key.toString());
            caches.put(key, t);
            return t;
        } finally {
            user.unlock();
        }
    }

    @Override public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<T> defaultValue) {
        try {
            user.lock();
            T t = get(key, type);
            if (t == null) {
                t = defaultValue.get();
                set(key, type, t);
            }
            return t;
        } finally {
            user.unlock();
        }
    }

    @Override public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        user.lock();
        if (!this.data.contains(key.toString())) {
            set(key, type, data);
        }
        user.unlock();
    }

    @Override public boolean has(@NotNull Key key) {
        try {
            user.lock();
            return data.contains(key.toString());
        } finally {
            user.unlock();
        }
    }

    @Override public void clear() {
        loadFromJsonDocument(Document.newJsonDocument());
    }

    @Override public void loadFromJsonDocument(Document document) {
        try {
            user.lock();
            this.data.clear();
            this.caches.clear();
            this.data.append(document);
        } finally {
            user.unlock();
        }
        notifyNotifiers();
    }

    public void merge(Document data) {
        try {
            user.lock();
            this.data.append(data);
            this.caches.clear();
        } finally {
            user.unlock();
        }
        notifyNotifiers();
    }

    @Override public Document storeToJsonDocument() {
        try {
            user.lock();
            return data.immutableCopy();
        } finally {
            user.unlock();
        }
    }

    @Override public void clearCache() {
        try {
            user.lock();
            caches.clear();
        } finally {
            user.unlock();
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

    private void notifyNotifiers() {
        for (UpdateNotifier updateNotifier : updateNotifiers) {
            updateNotifier.notify(this);
        }
    }
}
