/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.module.util.data;

import com.google.common.collect.HashBiMap;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataClearSet;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataRemove;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeDataSet;
import eu.darkcube.system.util.data.packets.PacketWrapperNodeQuery;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedPersistentDataStorages {
    static final Database database;
    private static final ReferenceQueue<SynchronizedPersistentDataStorage> queue =
            new ReferenceQueue<>();
    private static final Lock lock = new ReentrantLock(false);
    private static final HashBiMap<Key, Reference<? extends SynchronizedPersistentDataStorage>>
            storages = HashBiMap.create();

    static {
        database =
                CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("persistent_data");
        PacketAPI.getInstance().registerHandler(PacketWrapperNodeDataSet.class, new HandlerSet());
        PacketAPI.getInstance()
                .registerHandler(PacketWrapperNodeDataClearSet.class, new HandlerClearSet());
        PacketAPI.getInstance()
                .registerHandler(PacketWrapperNodeDataRemove.class, new HandlerRemove());
        PacketAPI.getInstance().registerHandler(PacketWrapperNodeQuery.class, new HandlerQuery());
        new CollectorHandler();
        if (CloudNetDriver.getInstance().getDatabaseProvider()
                .containsDatabase("plugin_persistent_data")) {
            Database db = CloudNetDriver.getInstance().getDatabaseProvider()
                    .getDatabase("plugin_persistent_data");
            for (String key : new ArrayList<>(db.keys())) {
                database.insert(new Key(key, key).toString(), db.get(key));
            }
            CloudNetDriver.getInstance().getDatabaseProvider()
                    .deleteDatabase("plugin_persistent_data");
        }
    }

    public static SynchronizedPersistentDataStorage storage(Key key) {
        lock.lock();
        Reference<? extends SynchronizedPersistentDataStorage> ref;
        ref = storages.getOrDefault(key, null);
        SynchronizedPersistentDataStorage storage = ref == null ? null : ref.get();
        if (storage == null) {
            storage = new SynchronizedPersistentDataStorage(key);
            if (database.contains(key.key())) {
                JsonDocument doc = database.get(key.key());
                database.delete(key.key());
                database.insert(key.toString(), doc);
                storage.loadFromJsonDocument(doc);
            } else if (database.contains(key.toString())) {
                storage.loadFromJsonDocument(database.get(key.toString()));
            }
            ref = new SoftReference<>(storage);
            storages.put(key, ref);
        }
        lock.unlock();
        return storage;
    }

    public static void init() {
    }

    private static class CollectorHandler extends Thread {
        public CollectorHandler() {
            setName("SynchronizedStorageGCHandler");
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Reference<? extends SynchronizedPersistentDataStorage> reference =
                            queue.remove();
                    lock.lock();
                    storages.inverse().remove(reference);
                    lock.unlock();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
