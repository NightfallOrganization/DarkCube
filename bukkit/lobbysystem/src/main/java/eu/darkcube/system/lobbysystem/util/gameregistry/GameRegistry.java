/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util.gameregistry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.lobbysystem.Lobby;

public class GameRegistry {
    private static final Database database = InjectionLayer.boot().instance(DatabaseProvider.class).database("lobbysystem_game_registry");

    private final Map<String, Collection<RegistryEntry>> entries = new HashMap<>();

    public GameRegistry(Lobby lobby) {
        var gson = new Gson();
        for (var entry : database.entries().entrySet()) {
            var taskName = entry.getKey();
            List<RegistryEntry> l = new ArrayList<>();
            var doc = entry.getValue();
            var documents = doc.readObject("entries", Document[].class);
            for (Document document : documents) {
                var data = document.getString("data");
                var protocol = document.readDocument("protocol");
                var protocolObject = gson.fromJson(protocol.serializeToString(), JsonObject.class);
                var registryEntry = new RegistryEntry(taskName, data, protocolObject);
                l.add(registryEntry);
            }
            if (!l.isEmpty()) {
                entries.put(taskName, l);
            }
        }
    }

    private void save(String taskName, Collection<RegistryEntry> entries) {
        var doc = Document.newJsonDocument();
        var docs = new Document[entries.size()];
        int i = 0;
        for (var entry : entries) {
            docs[i++] = Document.newJsonDocument().append("data", entry.data()).append("protocol", entry.protocol());
        }
        doc.append("entries", docs);
        database.insert(taskName, doc);
    }

    public @Nullable RegistryEntry entry(String taskName, String data) {
        synchronized (entries) {
            var l = entries.get(taskName);
            if (l == null) return null;
            for (var entry : l) {
                if (!entry.data().equals(data)) continue;
                return entry;
            }
            return null;
        }
    }

    public void removeEntry(String taskName, String data) {
        synchronized (entries) {
            var l = entries.get(taskName);
            var it = l.iterator();
            while (it.hasNext()) {
                var entry = it.next();
                if (entry.data().equals(data)) {
                    it.remove();
                    save(taskName, l);
                    return;
                }
            }
        }
        throw new IllegalArgumentException("Data not found: " + data);
    }

    public void addEntry(String taskName, String data, JsonObject protocol) {
        var entry = new RegistryEntry(taskName, data, protocol);
        synchronized (entries) {
            var l = entries.computeIfAbsent(taskName, s -> new ArrayList<>());
            l.add(entry);
            save(taskName, l);
        }
    }

    public @Unmodifiable Map<String, @Unmodifiable Collection<RegistryEntry>> entries() {
        synchronized (entries) {
            return Map.copyOf(entries);
        }
    }

    public @Unmodifiable Collection<RegistryEntry> entries(String taskName) {
        synchronized (entries) {
            return Set.copyOf(entries.getOrDefault(taskName, Collections.emptySet()));
        }
    }
}
