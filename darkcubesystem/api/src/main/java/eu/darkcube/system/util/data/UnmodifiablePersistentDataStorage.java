/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.function.Supplier;

public class UnmodifiablePersistentDataStorage implements PersistentDataStorage {
    private final PersistentDataStorage storage;

    public UnmodifiablePersistentDataStorage(PersistentDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public @UnmodifiableView @NotNull PersistentDataStorage unmodifiable() {
        return this;
    }

    @Override
    public @Unmodifiable @NotNull Collection<Key> keys() {
        return storage.keys();
    }

    @Override
    public <T> void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type) {
        return storage.get(key, type);
    }

    @Override
    public <T> @NotNull T get(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull Supplier<@NotNull T> defaultValue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean has(@NotNull Key key) {
        return storage.has(key);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadFromJsonDocument(JsonDocument document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonDocument storeToJsonDocument() {
        return storage.storeToJsonDocument();
    }

    @Override
    public @UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers() {
        return storage.updateNotifiers();
    }

    @Override
    public void addUpdateNotifier(@NotNull UpdateNotifier notifier) {
        storage.addUpdateNotifier(notifier);
    }

    @Override
    public void removeUpdateNotifier(@NotNull UpdateNotifier notifier) {
        storage.removeUpdateNotifier(notifier);
    }
}
