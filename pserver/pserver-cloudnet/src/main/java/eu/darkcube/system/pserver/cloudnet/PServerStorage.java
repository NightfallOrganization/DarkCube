/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.pserver.common.PServerPersistentDataStorage;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.LocalPersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PServerStorage implements PServerPersistentDataStorage {
    private final LocalPersistentDataStorage storage;
    private final NodePServerProvider provider;
    private final NodePServerExecutor executor;
    private final AtomicBoolean saving = new AtomicBoolean(false);
    private final AtomicBoolean saveAgain = new AtomicBoolean(false);
    private final PersistentDataType<Document> unsafeStorageModify = new PersistentDataType<>() {
        @Override public Document deserialize(Document doc, String key) {
            Document.Mutable mutableDoc = doc.mutableCopy();
            for (String docKey : doc.keys()) {
                if (!docKey.equals(key)) {
                    mutableDoc.remove(docKey);
                }
            }
            return mutableDoc;
        }

        @Override public void serialize(Document.Mutable doc, String key, Document data) {
            doc.append(data);
        }

        @Override public Document clone(Document object) {
            return object.immutableCopy();
        }
    };

    public PServerStorage(NodePServerProvider provider, NodePServerExecutor executor) {
        this.provider = provider;
        this.executor = executor;
        var pserver = executor.id();
        storage = new LocalPersistentDataStorage();
        if (!provider.pserverData().contains(pserver.toString())) {
            provider.pserverData().insert(pserver.toString(), Document.newJsonDocument());
        }
        storage.loadFromJsonDocument(provider.pserverData().get(pserver.toString()));
        storage.addUpdateNotifier(storage -> save());
    }

    private void save() {
        if (saving.compareAndSet(false, true)) {
            save0();
        } else {
            saveAgain.set(true);
            if (saving.compareAndSet(false, true)) {
                saveAgain.set(false);
                save0();
            }
        }
    }

    private void save0() {
        var doc = storeToJsonDocument();
        CompletableFuture<Boolean> fut = provider.pserverData().insertAsync(executor.id().toString(), doc);
        fut.exceptionally(th -> {
            th.printStackTrace();
            return null;
        }).thenAccept(suc -> {
            if (saveAgain.compareAndSet(true, false)) {
                save0();
            } else saving.set(false);
        });
    }

    @Override public @NotNull @Unmodifiable Collection<Key> keys() {
        return storage.keys();
    }

    @Override public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        storage.set(key, type, data);
    }

    @Override public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return storage.remove(key, type);
    }

    @Override public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return storage.get(key, type);
    }

    @Override public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return storage.get(key, type, defaultValue);
    }

    @Override public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        storage.setIfNotPresent(key, type, data);
    }

    @Override public boolean has(@NotNull Key key) {
        return storage.has(key);
    }

    @Override public void clear() {
        storage.clear();
    }

    @Override public void loadFromJsonDocument(Document document) {
        storage.loadFromJsonDocument(document);
    }

    @Override public Document storeToJsonDocument() {
        return storage.storeToJsonDocument();
    }

    @Override public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return storage.updateNotifiers();
    }

    @Override public void clearCache() {
        storage.clearCache();
    }

    @Override public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        storage.addUpdateNotifier(notifier);
    }

    @Override public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        storage.removeUpdateNotifier(notifier);
    }

    public void append(Document document) {
//        if (pserver.toString().startsWith("2a")) System.out.println(storage.storeToJsonDocument());
        storage.appendDocument(document);
//        if (pserver.toString().startsWith("2a")) System.out.println(storage.storeToJsonDocument());
    }

    public Document remove(Key key) {
        return storage.remove(key, unsafeStorageModify);
    }

    public Document getDef(Key key, Document def) {
        return storage.get(key, unsafeStorageModify, () -> def);
    }

    public Document get(Key key) {
        return storage.get(key, unsafeStorageModify);
    }

    public void setIfNotPresent(Key key, Document data) {
        storage.setIfNotPresent(key, unsafeStorageModify, data);
    }

    @Override public @NotNull CompletableFuture<@NotNull @Unmodifiable Collection<Key>> keysAsync() {
        return CompletableFuture.completedFuture(storage.keys());
    }

    @Override public @NotNull <T> CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        set(key, type, data);
        return CompletableFuture.completedFuture(null);
    }

    @Override public @NotNull <T> CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return CompletableFuture.completedFuture(remove(key, type));
    }

    @Override public @NotNull <T> CompletableFuture<@Nullable T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return CompletableFuture.completedFuture(get(key, type));
    }

    @Override
    public @NotNull <T> CompletableFuture<@NotNull T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        return CompletableFuture.completedFuture(get(key, type, defaultValue));
    }

    @Override
    public @NotNull <T> CompletableFuture<Void> setIfNotPresentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        setIfNotPresent(key, type, data);
        return CompletableFuture.completedFuture(null);
    }

    @Override public @NotNull CompletableFuture<Boolean> hasAsync(@NotNull Key key) {
        return CompletableFuture.completedFuture(has(key));
    }

    @Override public @NotNull CompletableFuture<Void> clearAsync() {
        clear();
        return CompletableFuture.completedFuture(null);
    }

    @Override public @NotNull CompletableFuture<Void> clearCacheAsync() {
        storage.clearCache();
        return CompletableFuture.completedFuture(null);
    }

    @Override public @NotNull CompletableFuture<Void> loadFromJsonDocumentAsync(@NotNull Document document) {
        loadFromJsonDocument(document);
        return CompletableFuture.completedFuture(null);
    }

    @Override public @NotNull CompletableFuture<@NotNull Document> storeToJsonDocumentAsync() {
        return CompletableFuture.completedFuture(storeToJsonDocument());
    }
}
