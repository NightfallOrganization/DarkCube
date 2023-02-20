/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.pserver.common.PServerPersistentDataStorage;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.LocalPersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class PServerStorage implements PServerPersistentDataStorage {
	private final LocalPersistentDataStorage storage;
	private final NodePServerProvider provider;
	private final UniqueId pserver;
	private final AtomicBoolean saving = new AtomicBoolean(false);
	private final AtomicBoolean saveAgain = new AtomicBoolean(false);
	private final PersistentDataType<JsonDocument> unsafeStorageModify =
			new PersistentDataType<JsonDocument>() {
				@Override
				public JsonDocument deserialize(JsonDocument doc, String key) {
					doc = doc.clone();
					for (String docKey : new ArrayList<>(doc.keys())) {
						if (!docKey.equals(key)) {
							doc.remove(docKey);
						}
					}
					return doc;
				}

				@Override
				public void serialize(JsonDocument doc, String key, JsonDocument data) {
					doc.append(data);
				}

				@Override
				public JsonDocument clone(JsonDocument object) {
					return object.clone();
				}
			};

	public PServerStorage(NodePServerProvider provider, UniqueId pserver) {
		this.provider = provider;
		this.pserver = pserver;
		storage = new LocalPersistentDataStorage();
		if (!provider.pserverData().contains(pserver.toString())) {
			provider.pserverData().insert(pserver.toString(), JsonDocument.newDocument());
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
		provider.pserverData().updateAsync(pserver.toString(), storeToJsonDocument())
				.addListener(new ITaskListener<Boolean>() {
					@Override
					public void onComplete(ITask<Boolean> task, Boolean aBoolean) {
						if (saveAgain.compareAndSet(true, false)) {
							save0();
						} else
							saving.set(false);
					}

					@Override
					public void onCancelled(ITask<Boolean> task) {
						if (saveAgain.compareAndSet(true, false)) {
							save0();
						} else
							saving.set(false);
						new CancellationException("Task cancelled").printStackTrace();
					}

					@Override
					public void onFailure(ITask<Boolean> task, Throwable th) {
						if (saveAgain.compareAndSet(true, false)) {
							save0();
						} else
							saving.set(false);
						th.printStackTrace();
					}
				});
	}

	@Override
	public @NotNull @Unmodifiable Collection<Key> keys() {
		return storage.keys();
	}

	@Override
	public <T> void set(Key key, PersistentDataType<T> type, T data) {
		storage.set(key, type, data);
	}

	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		return storage.remove(key, type);
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		return storage.get(key, type);
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<@NotNull T> defaultValue) {
		return storage.get(key, type, defaultValue);
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
		storage.setIfNotPresent(key, type, data);
	}

	@Override
	public boolean has(Key key) {
		return storage.has(key);
	}

	@Override
	public void clear() {
		storage.clear();
	}

	@Override
	public void loadFromJsonDocument(JsonDocument document) {
		storage.loadFromJsonDocument(document);
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

	public void append(JsonDocument document) {
		storage.appendDocument(document);
	}

	public JsonDocument remove(Key key) {
		return storage.remove(key, unsafeStorageModify);
	}

	public JsonDocument getDef(Key key, JsonDocument def) {
		return storage.get(key, unsafeStorageModify, () -> def);
	}

	public JsonDocument get(Key key) {
		return storage.get(key, unsafeStorageModify);
	}

	public void setIfNotPresent(Key key, JsonDocument data) {
		storage.setIfNotPresent(key, unsafeStorageModify, data);
	}

	@Override
	public CompletableFuture<@NotNull @Unmodifiable Collection<Key>> keysAsync() {
		return CompletableFuture.completedFuture(storage.keys());
	}

	@Override
	public @NotNull <T> CompletableFuture<Void> setAsync(Key key, PersistentDataType<T> type,
			T data) {
		set(key, type, data);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public @NotNull <T> CompletableFuture<@Nullable T> removeAsync(Key key,
			PersistentDataType<T> type) {
		return CompletableFuture.completedFuture(remove(key, type));
	}

	@Override
	public @NotNull <T> CompletableFuture<@Nullable T> getAsync(Key key,
			PersistentDataType<T> type) {
		return CompletableFuture.completedFuture(get(key, type));
	}

	@Override
	public @NotNull <T> CompletableFuture<@NotNull T> getAsync(Key key, PersistentDataType<T> type,
			Supplier<@NotNull T> defaultValue) {
		return CompletableFuture.completedFuture(get(key, type, defaultValue));
	}

	@Override
	public @NotNull <T> CompletableFuture<Void> setIfNotPresentAsync(Key key,
			PersistentDataType<T> type, T data) {
		setIfNotPresent(key, type, data);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Boolean> hasAsync(Key key) {
		return CompletableFuture.completedFuture(has(key));
	}

	@Override
	public CompletableFuture<Void> clearAsync() {
		clear();
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<Void> loadFromJsonDocumentAsync(JsonDocument document) {
		loadFromJsonDocument(document);
		return CompletableFuture.completedFuture(null);
	}

	@Override
	public CompletableFuture<@NotNull JsonDocument> storeToJsonDocumentAsync() {
		return CompletableFuture.completedFuture(storeToJsonDocument());
	}
}
