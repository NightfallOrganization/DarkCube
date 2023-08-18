/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.common;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.UnmodifiablePersistentDataStorage;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface PServerPersistentDataStorage extends PersistentDataStorage {

    @Override default @NotNull @UnmodifiableView PServerPersistentDataStorage unmodifiable() {
        return new UnmodifiableStorage(this);
    }

    @NotNull CompletableFuture<@NotNull @Unmodifiable Collection<Key>> keysAsync();

    /**
     * Saves some data
     *
     * @param key  the key
     * @param type the type
     * @param data the data
     * @param <T>  the data type
     */
    <T> @NotNull CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

    /**
     * Removes some data
     *
     * @param key  the key
     * @param type the type
     * @param <T>  the data type
     * @return the removed data
     */
    <T> @NotNull CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type);

    /**
     * @param key  the key
     * @param type the type
     * @param <T>  the data type
     * @return saved data, null if not present
     */
    <T> @NotNull CompletableFuture<@Nullable T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type);

    /**
     * @param key          the key
     * @param type         the type
     * @param defaultValue the default value
     * @param <T>          the data type
     * @return the saved data, defaultValue if not present
     */
    <T> @NotNull CompletableFuture<@NotNull T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue);

    /**
     * @param key  the key
     * @param type the type
     * @param data the data
     * @param <T>  the data type
     */
    <T> @NotNull CompletableFuture<Void> setIfNotPresentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

    /**
     * @param key the key
     * @return whether data is present for the given key
     */
    @NotNull CompletableFuture<Boolean> hasAsync(@NotNull Key key);

    /**
     * Clears this storage
     */
    @NotNull CompletableFuture<Void> clearAsync();

    @NotNull CompletableFuture<Void> clearCacheAsync();

    /**
     * Loads all the data from a {@link JsonDocument}<br>
     * <b>This will NOT be cleared, the data will be ADDED to the current data</b>
     *
     * @param document the document to load the data from
     */
    @NotNull CompletableFuture<Void> loadFromJsonDocumentAsync(@NotNull Document document);

    /**
     * @return a jsonDocument with all the data
     */
    @NotNull CompletableFuture<@NotNull Document> storeToJsonDocumentAsync();

    class UnmodifiableStorage extends UnmodifiablePersistentDataStorage implements PServerPersistentDataStorage {
        private final PServerPersistentDataStorage storage;

        public UnmodifiableStorage(PServerPersistentDataStorage storage) {
            super(storage);
            this.storage = storage;
        }

        @Override public @UnmodifiableView @NotNull PServerPersistentDataStorage unmodifiable() {
            return this;
        }

        @Override public @NotNull CompletableFuture<@NotNull @Unmodifiable Collection<Key>> keysAsync() {
            return storage.keysAsync();
        }

        @Override
        public @NotNull <T> CompletableFuture<Void> setAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
            throw new UnsupportedOperationException();
        }

        @Override public @NotNull <T> CompletableFuture<@Nullable T> removeAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
            throw new UnsupportedOperationException();
        }

        @Override public @NotNull <T> CompletableFuture<@Nullable T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type) {
            return storage.getAsync(key, type);
        }

        @Override
        public @NotNull <T> CompletableFuture<@NotNull T> getAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull <T> CompletableFuture<Void> setIfNotPresentAsync(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
            throw new UnsupportedOperationException();
        }

        @Override public @NotNull CompletableFuture<Boolean> hasAsync(@NotNull Key key) {
            return storage.hasAsync(key);
        }

        @Override public @NotNull CompletableFuture<Void> clearAsync() {
            throw new UnsupportedOperationException();
        }

        @Override public @NotNull CompletableFuture<Void> clearCacheAsync() {
            throw new UnsupportedOperationException();
        }

        @Override public @NotNull CompletableFuture<Void> loadFromJsonDocumentAsync(@NotNull Document document) {
            throw new UnsupportedOperationException();
        }

        @Override public @NotNull CompletableFuture<@NotNull Document> storeToJsonDocumentAsync() {
            return storage.storeToJsonDocumentAsync();
        }
    }
}
