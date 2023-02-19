/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.module.util.data;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataStorage;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataClearSet;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataRemove;
import eu.darkcube.system.util.data.packets.PacketNodeWrapperDataSet;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * A data storage that is synchronized over the entire cloud system
 */
public class SynchronizedPersistentDataStorage implements PersistentDataStorage {
	private final Key key;
	private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
	private final JsonDocument data = new JsonDocument();
	private final Map<Key, Object> cache = new HashMap<>();
	private final Collection<@NotNull UpdateNotifier> updateNotifiers =
			new CopyOnWriteArrayList<>();
	private final AtomicBoolean saving = new AtomicBoolean(false);
	private final AtomicBoolean saveAgain = new AtomicBoolean(false);

	SynchronizedPersistentDataStorage(Key key) {
		this.key = key;
	}

	@Override
	public Collection<Key> keys() {
		List<Key> keys = new ArrayList<>();
		try {
			lock.readLock().lock();
			for (String s : data.keys()) {
				keys.add(Key.fromString(s));
			}
		} finally {
			lock.readLock().unlock();
		}
		return Collections.unmodifiableCollection(keys);
	}

	@Override
	public <T> void set(Key key, PersistentDataType<T> type, T data) {
		try {
			lock.writeLock().lock();
			data = type.clone(data);
			if (cache.containsKey(key) && cache.get(key).equals(data)) {
				return;
			}
			cache.put(key, data);
			JsonDocument d = JsonDocument.newDocument();
			type.serialize(d, key.toString(), data);
			this.data.append(d);
			new PacketNodeWrapperDataSet(this.key, d).sendAsync();
		} finally {
			lock.writeLock().unlock();
		}
		notifyNotifiers();
	}

	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		T ret;
		try {
			lock.writeLock().lock();
			if (!data.contains(key.toString())) {
				return null;
			}
			@SuppressWarnings("unchecked")
			T old = (T) cache.remove(key);
			if (old == null && type != null) {
				old = type.deserialize(data, key.toString());
			}
			data.remove(key.toString());
			new PacketNodeWrapperDataRemove(this.key, key).sendAsync();
			ret = type != null ? type.clone(old) : null;
		} finally {
			lock.writeLock().unlock();
		}
		notifyNotifiers();
		return ret;
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type) {
		try {
			lock.readLock().lock();
			if (cache.containsKey(key)) {
				//noinspection unchecked
				return type.clone((T) cache.get(key));
			}
		} finally {
			lock.readLock().unlock();
		}
		try {
			lock.writeLock().lock();
			if (cache.containsKey(key)) {
				//noinspection unchecked
				return type.clone((T) cache.get(key));
			}
			if (!data.contains(key.toString())) {
				return null;
			}
			T value = type.clone(type.deserialize(data, key.toString()));
			cache.put(key, value);
			return type.clone(value);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public <T> T get(Key key, PersistentDataType<T> type, Supplier<T> defaultValue) {
		try {
			lock.readLock().lock();
			if (cache.containsKey(key)) {
				//noinspection unchecked
				return type.clone((T) cache.get(key));
			}
		} finally {
			lock.readLock().unlock();
		}
		T ret;
		try {
			lock.writeLock().lock();
			if (cache.containsKey(key)) {
				//noinspection unchecked
				return type.clone((T) cache.get(key));
			}
			if (data.contains(key.toString())) {
				T value = type.clone(type.deserialize(data, key.toString()));
				cache.put(key, value);
				return type.clone(value);
			}
			T val = type.clone(defaultValue.get());
			JsonDocument d = JsonDocument.newDocument();
			type.serialize(d, key.toString(), val);
			this.data.append(d);
			new PacketNodeWrapperDataSet(this.key, d).sendAsync();
			cache.put(key, val);
			ret = type.clone(val);
		} finally {
			lock.writeLock().unlock();
		}
		notifyNotifiers();
		return ret;
	}

	@Override
	public <T> void setIfNotPresent(Key key, PersistentDataType<T> type, T data) {
		try {
			lock.readLock().lock();
			if (this.data.contains(key.toString())) {
				return;
			}
		} finally {
			lock.readLock().unlock();
		}
		try {
			lock.writeLock().lock();
			if (this.data.contains(key.toString())) {
				return;
			}
			data = type.clone(data);
			JsonDocument d = JsonDocument.newDocument();
			type.serialize(d, key.toString(), data);
			this.data.append(d);
			new PacketNodeWrapperDataSet(this.key, d).sendAsync();
			cache.put(key, data);
		} finally {
			lock.writeLock().unlock();
		}
		notifyNotifiers();
	}

	@Override
	public boolean has(Key key) {
		try {
			lock.readLock().lock();
			return data.contains(key.toString());
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void clear() {
		try {
			lock.writeLock().lock();
			data.clear();
			cache.clear();
			new PacketNodeWrapperDataClearSet(key, JsonDocument.newDocument()).sendAsync();
		} finally {
			lock.writeLock().unlock();
		}
		notifyNotifiers();
	}

	@Override
	public void loadFromJsonDocument(JsonDocument document) {
		try {
			lock.writeLock().lock();
			data.clear();
			cache.clear();
			document = document.clone();
			data.append(document);
			new PacketNodeWrapperDataClearSet(key, document).sendAsync();
		} finally {
			lock.writeLock().unlock();
		}
		notifyNotifiers();
	}

	@Override
	public JsonDocument storeToJsonDocument() {
		try {
			lock.readLock().lock();
			return JsonDocument.newDocument().append(data);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public @UnmodifiableView Collection<@NotNull UpdateNotifier> updateNotifiers() {
		return Collections.unmodifiableCollection(updateNotifiers);
	}

	private void save() {
		if (saving.compareAndSet(false, true)) {
			SynchronizedPersistentDataStorages.database.containsAsync(key.toString())
					.addListener(new ITaskListener<Boolean>() {
						@Override
						public void onComplete(ITask<Boolean> task, Boolean aBoolean) {
							if (aBoolean) {
								saveAgain.set(false);
								SynchronizedPersistentDataStorages.database.updateAsync(
												key.toString(), storeToJsonDocument())
										.addListener(new ITaskListener<Boolean>() {
											@Override
											public void onComplete(ITask<Boolean> task,
													Boolean aBoolean) {
												saving.set(false);
												if (saveAgain.compareAndSet(true, false)) {
													save();
												}
											}

											@Override
											public void onCancelled(ITask<Boolean> task) {
												new Error("Task cancelled").printStackTrace();
											}

											@Override
											public void onFailure(ITask<Boolean> task,
													Throwable th) {
												th.printStackTrace();
												saving.set(false);
												save();
											}
										});
							} else {
								saveAgain.set(false);
								SynchronizedPersistentDataStorages.database.insertAsync(
												key.toString(), storeToJsonDocument())
										.addListener(new ITaskListener<Boolean>() {
											@Override
											public void onComplete(ITask<Boolean> task,
													Boolean aBoolean) {
												saving.set(false);
												if (saveAgain.compareAndSet(true, false)) {
													save();
												}
											}

											@Override
											public void onCancelled(ITask<Boolean> task) {
												new Error("Task cancelled").printStackTrace();
											}

											@Override
											public void onFailure(ITask<Boolean> task,
													Throwable th) {
												th.printStackTrace();
												saving.set(false);
												save();
											}
										});
							}
						}

						@Override
						public void onCancelled(ITask<Boolean> task) {
							new Error("Task cancelled").printStackTrace();
						}

						@Override
						public void onFailure(ITask<Boolean> task, Throwable th) {
							th.printStackTrace();
							saving.set(false);
							save();
						}
					});
		} else {
			saveAgain.set(true);
			if (!saving.get()) {
				save();
			}
		}
	}

	private void notifyNotifiers() {
		save(); // Do this here cuz were lazy
		for (UpdateNotifier updateNotifier : updateNotifiers) {
			updateNotifier.notify(this);
		}
	}

	public Key key() {
		return key;
	}

	@Override
	public void addUpdateNotifier(UpdateNotifier notifier) {
		updateNotifiers.add(notifier);
	}

	@Override
	public void removeUpdateNotifier(UpdateNotifier notifier) {
		updateNotifiers.remove(notifier);
	}
}
