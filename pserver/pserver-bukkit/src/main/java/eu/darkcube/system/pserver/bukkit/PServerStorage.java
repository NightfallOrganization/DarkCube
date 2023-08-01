/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.pserver.common.PServerPersistentDataStorage;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.packets.wn.storage.*;
import eu.darkcube.system.pserver.common.packets.wn.storage.PacketKeys.Response;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class PServerStorage implements PServerPersistentDataStorage {
    private final UniqueId pserver;
    private final Collection<UpdateNotifier> notifiers = new CopyOnWriteArrayList<>();

    public PServerStorage(UniqueId pserver) {
        this.pserver = pserver;
    }

    private static <T> T get(CompletableFuture<T> future) {
        try {
            return future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> CompletableFuture<T> wrap(ITask<T> task) {
        CompletableFuture<T> fut = new CompletableFuture<>();
        task.onComplete(fut::complete).onCancelled(
                        t -> fut.completeExceptionally(new CancellationException("Task cancelled")))
                .onFailure(fut::completeExceptionally);
        return fut;
    }

    @Override
    public @Unmodifiable @NotNull Collection<Key> keys() {
        return get(keysAsync());
    }

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        get(setAsync(key, type, data));
    }

    @Override
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return get(removeAsync(key, type));
    }

    @Override
    public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return get(getAsync(key, type));
    }

    @Override
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return get(getAsync(key, type, defaultValue));
    }

    @Override
    public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        get(setIfNotPresentAsync(key, type, data));
    }

    @Override
    public boolean has(@NotNull Key key) {
        return Boolean.TRUE.equals(get(hasAsync(key)));
    }

    @Override
    public void clear() {
        get(clearAsync());
    }

    @Override
    public void loadFromJsonDocument(JsonDocument document) {
        get(loadFromJsonDocumentAsync(document));
    }

    @Override
    public JsonDocument storeToJsonDocument() {
        return get(storeToJsonDocumentAsync());
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return Collections.unmodifiableCollection(notifiers);
    }

    @Override
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        notifiers.add(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        notifiers.remove(notifier);
    }

    @Override
    public CompletableFuture<@NotNull @Unmodifiable Collection<Key>> keysAsync() {
        return wrap(new PacketKeys(pserver).sendQueryAsync(PacketKeys.Response.class)).thenApply(
                Response::keys);
    }

    @Override
    public @NotNull <T> CompletableFuture<Void> setAsync(Key key, PersistentDataType<T> type,
                                                         T data) {
        JsonDocument document = JsonDocument.newDocument();
        type.serialize(document, key.toString(), data);
        return wrap(new PacketSet(pserver, document).sendQueryAsync(PacketSet.Response.class)
                .map(p -> null));
    }

    @Override
    public @NotNull <T> CompletableFuture<@Nullable T> removeAsync(Key key,
                                                                   PersistentDataType<T> type) {
        return wrap(new PacketRemove(pserver, key).sendQueryAsync(PacketRemove.Response.class)
                .map(p -> {
                    if (p.data() != null) {
                        return type.deserialize(p.data(), key.toString());
                    }
                    return null;
                }));
    }

    @Override
    public @NotNull <T> CompletableFuture<@Nullable T> getAsync(Key key,
                                                                PersistentDataType<T> type) {
        return wrap(new PacketGet(pserver, key).sendQueryAsync(PacketGet.Response.class).map(p -> {
            if (p.data() != null) {
                return type.deserialize(p.data(), key.toString());
            }
            return null;
        }));
    }

    @Override
    public @NotNull <T> CompletableFuture<@NotNull T> getAsync(Key key, PersistentDataType<T> type,
                                                               Supplier<@NotNull T> defaultValue) {
        JsonDocument doc = JsonDocument.newDocument();
        type.serialize(doc, key.toString(), defaultValue.get());
        return wrap(new PacketGetDef(pserver, key, doc).sendQueryAsync(PacketGetDef.Response.class)
                .map(p -> {
                    if (p.data() != null) {
                        return type.deserialize(p.data(), key.toString());
                    }
                    return null;
                }));
    }

    @Override
    public @NotNull <T> CompletableFuture<Void> setIfNotPresentAsync(Key key,
                                                                     PersistentDataType<T> type, T data) {
        JsonDocument doc = JsonDocument.newDocument();
        type.serialize(doc, key.toString(), data);
        return wrap(new PacketSetIfNotPresent(pserver, key, doc).sendQueryAsync(
                PacketSetIfNotPresent.Response.class).map(p -> null));
    }

    @Override
    public CompletableFuture<@NotNull Boolean> hasAsync(Key key) {
        return wrap(new PacketHas(pserver, key).sendQueryAsync(PacketHas.Response.class)
                .map(PacketHas.Response::has));
    }

    @Override
    public CompletableFuture<Void> clearAsync() {
        return wrap(
                new PacketClear(pserver).sendQueryAsync(PacketClear.Response.class).map(p -> null));
    }

    @Override
    public CompletableFuture<Void> loadFromJsonDocumentAsync(JsonDocument document) {
        return wrap(new PacketLoadFromDocument(pserver, document.clone()).sendQueryAsync(
                PacketLoadFromDocument.Response.class).map(p -> null));
    }

    @Override
    public CompletableFuture<@NotNull JsonDocument> storeToJsonDocumentAsync() {
        return wrap(new PacketStoreToDocument(pserver).sendQueryAsync(
                PacketStoreToDocument.Response.class).map(PacketStoreToDocument.Response::data));
    }
}
