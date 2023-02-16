/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * @author DasBabyPixel
 */
public interface PersistentDataStorage {

	static PersistentDataStorage unmodifiable(PersistentDataStorage storage) {
		return new PersistentDataStorage() {
			@Override
			public Collection<Key> keys() {
				return storage.keys();
			}

			@Override
			public <T> void set(Key key, PersistentDataType<T> type, T data) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <T> T remove(Key key, PersistentDataType<T> type) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <T> T get(Key key, PersistentDataType<T> type) {
				return storage.get(key, type);
			}

			@Override
			public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
				throw new UnsupportedOperationException();
			}

			@Override
			public boolean has(Key key) {
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
			public @UnmodifiableView Collection<@NotNull UpdateNotifier> updateNotifiers() {
				return storage.updateNotifiers();
			}

			@Override
			public void addUpdateNotifier(UpdateNotifier notifier) {
				storage.addUpdateNotifier(notifier);
			}

			@Override
			public void removeUpdateNotifier(UpdateNotifier notifier) {
				storage.removeUpdateNotifier(notifier);
			}
		};
	}

	@NotNull @Unmodifiable Collection<Key> keys();

	/**
	 * Saves some data
	 *
	 * @param key  the key
	 * @param type the type
	 * @param data the data
	 * @param <T>  the data type
	 */
	<T>

	void set(@NotNull Key key, @NotNull PersistentDataType<T> type, @NotNull T data);

	/**
	 * Removes some data
	 *
	 * @param key  the key
	 * @param type the type
	 * @param <T>  the data type
	 *
	 * @return the removed data
	 */
	@Nullable <T> T remove(@NotNull Key key, @NotNull PersistentDataType<T> type);

	/**
	 * @param key  the key
	 * @param type the type
	 * @param <T>  the data type
	 *
	 * @return saved data, null if not present
	 */
	@Nullable <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type);

	/**
	 * @param key          the key
	 * @param type         the type
	 * @param defaultValue the default value
	 * @param <T>          the data type
	 *
	 * @return the saved data, defaultValue if not present
	 */
	@NotNull <T> T get(@NotNull Key key, @NotNull PersistentDataType<T> type,
			@NotNull Supplier<@NotNull T> defaultValue);

	/**
	 * @param key  the key
	 * @param type the type
	 * @param data the data
	 * @param <T>  the data type
	 */
	<T> void setIfNotPresent(@NotNull Key key, @NotNull PersistentDataType<T> type,
			@NotNull T data);

	/**
	 * @param key the key
	 *
	 * @return whether data is present for the given key
	 */
	boolean has(@NotNull Key key);

	/**
	 * Clears this storage
	 */
	void clear();

	/**
	 * Loads all the data from a {@link JsonDocument}<br>
	 * <b>This will NOT be cleared, the data will be ADDED to the current data</b>
	 *
	 * @param document the document to load the data from
	 */
	void loadFromJsonDocument(JsonDocument document);

	/**
	 * @return a jsonDocument with all the data
	 */
	JsonDocument storeToJsonDocument();

	/**
	 * @return an unmodifiable view of all {@link UpdateNotifier}s
	 */
	@UnmodifiableView @NotNull Collection<@NotNull UpdateNotifier> updateNotifiers();

	/**
	 * Adds an {@link UpdateNotifier} to this storage
	 *
	 * @param notifier the notifier
	 */
	void addUpdateNotifier(@NotNull UpdateNotifier notifier);

	/**
	 * Removes an {@link UpdateNotifier} from this storage
	 *
	 * @param notifier the notifier
	 */
	void removeUpdateNotifier(@NotNull UpdateNotifier notifier);

	/**
	 * This will be notified whenever the data of a {@link PersistentDataStorage} updates
	 */
	interface UpdateNotifier {
		void notify(PersistentDataStorage storage);
	}
}
