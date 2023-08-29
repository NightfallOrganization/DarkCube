/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.userapi;

import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.userapi.UserLocalPersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataStorage;

import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;

class NodeDataSaver {
    private final Database database;
    private final ConcurrentMap<UUID, Document> dataToSave = new ConcurrentHashMap<>();
    // Writes should be fast so if this queue gets filled with more than 256 entries, we can just let new entries wait
    private final BlockingDeque<UUID> saveQueue = new LinkedBlockingDeque<>(256);
    private final PersistentDataStorage.UpdateNotifier saveNotifier = storage -> save((UserLocalPersistentDataStorage) storage);
    private volatile boolean exit = false;

    public NodeDataSaver(Database database) {
        this.database = database;
    }

    public void exit() {
        exit = true;
        saveQueue.offer(UUID.randomUUID());
    }

    public void init() {
        Task.supply(() -> {
            while (!exit) {
                try {
                    UUID uniqueId = saveQueue.take();
                    Document data = dataToSave.remove(uniqueId);
                    if (data == null) continue;
                    database.insert(uniqueId.toString(), data);
                } catch (InterruptedException ignored) {
                }
            }
        });
    }

    public PersistentDataStorage.UpdateNotifier saveNotifier() {
        return saveNotifier;
    }

    void save(UserLocalPersistentDataStorage storage) {
        save(storage.uniqueId(), storage.storeToJsonDocument());
    }

    private void save(UUID uniqueId, Document document) {
        dataToSave.put(uniqueId, document); // Overwrite old entry to prevent clogging memory and/or the database
        saveQueue.offer(uniqueId);
    }
}
