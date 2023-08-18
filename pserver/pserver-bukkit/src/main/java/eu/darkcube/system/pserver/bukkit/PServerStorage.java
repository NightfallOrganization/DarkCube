/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import eu.cloudnetservice.common.concurrent.Task;
import eu.cloudnetservice.driver.document.Document;
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

    private static <T> CompletableFuture<T> wrap(Task<T> task) {
        return task;
    }

    @Override public @Unmodifiable @NotNull Collection<Key> keys() {
        return get(keysAsync());
    }

    @Override public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        get(setAsync(key, type, data));
    }

    @Override public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return get(removeAsync(key, type));
    }

    @Override public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return get(getAsync(key, type));
    }

    @Override public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return get(getAsync(key, type, defaultValue)); // TODO nullability
    }

    @Override public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        get(setIfNotPresentAsync(key, type, data));
    }

    @Override public boolean has(@NotNull Key key) {
        return Boolean.TRUE.equals(get(hasAsync(key)));
    }

    @Override public void clear() {
        get(clearAsync());
    }

    @Override public void loadFromJsonDocument(Document document) {
        get(loadFromJsonDocumentAsync(document));
    }

    @Override public Document storeToJsonDocument() {
        return get(storeToJsonDocumentAsync());
    }

    @Override public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return Collections.unmodifiableCollection(notifiers);
    }

    @Override public void clearCache() {
    }

    @Override public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        notifiers.add(notifier);
    }

    @Override public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        notifiers.remove(notifier);
    }

    @Override public @NotNull CompletableFuture<@NotNull @Unmodifiable Collection<Key>> keysAsync() {
        return wrap(new PacketKeys(pserver).sendQueryAsync(PacketKeys.Response.class)).thenApply(Response::keys);
    }

    @Override public @NotNull <T> CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        Document.Mutable document = Document.newJsonDocument();
        type.serialize(document, key.toString(), data);
        return new PacketSet(pserver, document).sendQueryAsync(PacketSet.Response.class).thenApply(p -> null);
    }

    @Override public @NotNull <T> CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return new PacketRemove(pserver, key).sendQueryAsync(PacketRemove.Response.class).thenApply(p -> {
            if (p.data() != null) {
                return type.deserialize(p.data(), key.toString());
            }
            return null;
        });
    }

    @Override public @NotNull <T> CompletableFuture<@Nullable T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return new PacketGet(pserver, key).sendQueryAsync(PacketGet.Response.class).thenApply(p -> {
            if (p.data() != null) {
                return type.deserialize(p.data(), key.toString());
            }
            return null;
        });
    }

    @Override
    public @NotNull <T> CompletableFuture<@NotNull T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        Document.Mutable doc = Document.newJsonDocument();
        type.serialize(doc, key.toString(), defaultValue.get());
        return new PacketGetDef(pserver, key, doc).sendQueryAsync(PacketGetDef.Response.class).thenApply(p -> {
            if (p.data() != null) {
                return type.deserialize(p.data(), key.toString());
            }
            return null;
        });
    }

    @Override
    public @NotNull <T> CompletableFuture<Void> setIfNotPresentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        Document.Mutable doc = Document.newJsonDocument();
        type.serialize(doc, key.toString(), data);
        return new PacketSetIfNotPresent(pserver, key, doc).sendQueryAsync(PacketSetIfNotPresent.Response.class).thenApply(p -> null);
    }

    @Override public @NotNull CompletableFuture<@NotNull Boolean> hasAsync(@NotNull Key key) {
        return new PacketHas(pserver, key).sendQueryAsync(PacketHas.Response.class).thenApply(PacketHas.Response::has);
    }

    @Override public @NotNull CompletableFuture<Void> clearAsync() {
        return new PacketClear(pserver).sendQueryAsync(PacketClear.Response.class).thenApply(p -> null);
    }

    @Override public @NotNull CompletableFuture<Void> clearCacheAsync() {
        return new PacketClearCache(pserver).sendQueryAsync(PacketClearCache.Response.class).thenApply(p -> null);
    }

    @Override public @NotNull CompletableFuture<Void> loadFromJsonDocumentAsync(@NotNull Document document) {
        return new PacketLoadFromDocument(pserver, document.immutableCopy())
                .sendQueryAsync(PacketLoadFromDocument.Response.class)
                .thenApply(p -> null);
    }

    @Override public @NotNull CompletableFuture<@NotNull Document> storeToJsonDocumentAsync() {
        return new PacketStoreToDocument(pserver)
                .sendQueryAsync(PacketStoreToDocument.Response.class)
                .thenApply(PacketStoreToDocument.Response::data);
    }
}
