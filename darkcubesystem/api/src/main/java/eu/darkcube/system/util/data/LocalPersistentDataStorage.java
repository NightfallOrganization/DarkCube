/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.util.data;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class LocalPersistentDataStorage implements PersistentDataStorage {
	private final ReadWriteLock lock = new ReentrantReadWriteLock(false);
	private final JsonDocument data = new JsonDocument();
	private final Map<Key, Object> cache = new HashMap<>();
	private final Collection<@NotNull UpdateNotifier> updateNotifiers = new ArrayList<>();

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
			type.serialize(this.data, key.toString(), data);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public <T> T remove(Key key, PersistentDataType<T> type) {
		try {
			lock.writeLock().lock();
			if (!data.contains(key.toString())) {
				return null;
			}
			@SuppressWarnings("unchecked")
			T old = (T) cache.remove(key);
			if (old == null) {
				old = type.deserialize(data, key.toString());
			}
			data.remove(key.toString());
			return type.clone(old);
		} finally {
			lock.writeLock().unlock();
		}
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
			type.serialize(data, key.toString(), val);
			cache.put(key, val);
			return val;
		} finally {
			lock.writeLock().unlock();
		}
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
			type.serialize(this.data, key.toString(), data);
			cache.put(key, data);
		} finally {
			lock.writeLock().unlock();
		}
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
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void loadFromJsonDocument(JsonDocument document) {
		try {
			lock.writeLock().lock();
			data.clear();
			cache.clear();
			data.append(document);
		} finally {
			lock.writeLock().unlock();
		}
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

	@Override
	public void addUpdateNotifier(UpdateNotifier notifier) {
		updateNotifiers.add(notifier);
	}

	@Override
	public void removeUpdateNotifier(UpdateNotifier notifier) {
		updateNotifiers.remove(notifier);
	}
}
