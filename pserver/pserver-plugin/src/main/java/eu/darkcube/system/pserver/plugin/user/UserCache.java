/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import eu.darkcube.system.pserver.plugin.PServerPlugin;
import eu.darkcube.system.util.AsyncExecutor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class UserCache {

	public static UserCache cache;

	private ConcurrentHashMap<UUID, Entry> byUUID = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Entry> byName = new ConcurrentHashMap<>();

	private final Path cacheFile;
	private AtomicReference<Future<?>> saveFuture = new AtomicReference<>();

	private final GsonBuilder builder = new GsonBuilder();
	private AtomicReference<Gson> gson = new AtomicReference<>(new Gson());
	private ExpiredUserCache expiredUserCache;

	protected UserCache(Path cacheFile, Path expiredCacheFile) {
		this.cacheFile = cacheFile;
		this.expiredUserCache = new ExpiredUserCache(expiredCacheFile);
		if (!Files.exists(cacheFile)) {
			try {
				Files.createFile(cacheFile);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			try {
				BufferedReader reader = Files.newBufferedReader(cacheFile, StandardCharsets.UTF_8);
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.isEmpty()) {
						continue;
					}
					try {
						Entry entry = deserialize(line);
						if (!isEntryValid(entry)) {
							this.expiredUserCache.byUUID.put(entry.uuid, entry);
							continue;
						}
						update(entry, false);
					} catch (Exception ex) {
						Bukkit.getConsoleSender()
								.sendMessage(ChatColor.RED + "Failed to deserialize user: " + line);
					}
				}
				this.save();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void save() {
		Runnable r = () -> {
			Collection<Entry> entries = byUUID.values();
			Iterator<Entry> it = entries.iterator();
			try {
				BufferedWriter writer = Files.newBufferedWriter(cacheFile, StandardCharsets.UTF_8,
						StandardOpenOption.CREATE, StandardOpenOption.WRITE,
						StandardOpenOption.TRUNCATE_EXISTING);
				while (it.hasNext()) {
					Entry entry = it.next();
					if (!isEntryValid(entry)) {
						removeEntry(entry);
						continue;
					}
					String entryString = entry.toString();
					writer.write(entryString);
					writer.newLine();
				}
				writer.flush();
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			expiredUserCache.save();
		};
		Future<?> future = saveFuture.get();
		if (future != null && !future.isDone()) {
			future.cancel(true);
		}
		saveFuture.set(AsyncExecutor.service().submit(r));
	}

	private void waitForFuture(Future<?> f) {
		if (f != null) {
			while (true) {
				try {
					break;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	public void waitForSaveToFinish() {
		waitForFuture(saveFuture.get());
	}

	private void doUnload() {
		save();
		waitForSaveToFinish();
	}

	public void updateGsonBuilder(Consumer<GsonBuilder> updater) {
		updater.accept(builder);
		gson.set(builder.create());
	}

	public boolean isEntryValid(Entry entry) {
		return !entry.isExpired();
	}

	public Entry update(Player player) {
		return update(createEntry(player), true);
	}

	public Entry update(Entry entry, boolean save) {
		if (isEntryValid(entry)) {
			byUUID.put(entry.uuid, entry);
			byName.put(entry.name, entry);
			this.expiredUserCache.byUUID.remove(entry.uuid);
		} else {
			removeEntry(entry);
		}
		if (save) {
			this.save();
		}
		return entry;
	}

	public Collection<UUID> getKeys() {
		return byUUID.keySet();
	}

	private void removeEntry(Entry entry) {
		byUUID.remove(entry.uuid, entry);
		byName.remove(entry.name, entry);
		this.expiredUserCache.byUUID.put(entry.uuid, entry);
	}

	public Entry getEntry(UUID uuid) {
		Entry entry = byUUID.get(uuid);
		return getEntry0(entry);
	}

	public Entry getEntry(String name) {
		Entry entry = byName.get(formatName(name));
		return getEntry0(entry);
	}

	private Entry getEntry0(Entry entry) {
		if (entry == null) {
			return null;
		}
		if (!isEntryValid(entry)) {
			removeEntry(entry);
			return null;
		}
		return entry;
	}

	private Entry createEntry(Player player) {
		if (byUUID.containsKey(player.getUniqueId())) {
			Entry entry = byUUID.get(player.getUniqueId());
			entry.expiresAt = Instant.now().plus(30, ChronoUnit.DAYS);
			return entry;
		}
		return new Entry(player.getName(), player.getUniqueId(),
				Instant.now().plus(30, ChronoUnit.DAYS), new JsonObject());
	}

	private String formatName(String name) {
		return name.toLowerCase(Locale.ROOT);
	}

	public class ExpiredUserCache {

		private final Path cacheFile;
		ConcurrentHashMap<UUID, Entry> byUUID = new ConcurrentHashMap<>();

		private void save() {
			try {
				BufferedWriter writer = Files.newBufferedWriter(cacheFile, StandardCharsets.UTF_8,
						StandardOpenOption.CREATE, StandardOpenOption.WRITE,
						StandardOpenOption.TRUNCATE_EXISTING);
				for (Entry entry : byUUID.values()) {
					writer.write(entry.toString());
					writer.newLine();
				}
				writer.flush();
				writer.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		public ExpiredUserCache(Path cacheFile) {
			this.cacheFile = cacheFile;
			if (Files.exists(cacheFile)) {
				try {
					BufferedReader reader =
							Files.newBufferedReader(cacheFile, StandardCharsets.UTF_8);
					String line;
					while ((line = reader.readLine()) != null) {
						if (line.isEmpty()) {
							continue;
						}
						try {
							Entry entry = deserialize(line);
							byUUID.put(entry.uuid, entry);
						} catch (Exception ex) {
							Bukkit.getConsoleSender().sendMessage(
									ChatColor.RED + "Failed to deserialize user: " + line);
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				try {
					Files.createFile(cacheFile);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}

	}


	public class Entry {

		public final String name;
		public final UUID uuid;
		public Instant expiresAt;
		public final JsonObject extra;

		public Entry(String name, UUID uuid, Instant expiresAt, JsonObject extra) {
			this.name = name;
			this.uuid = uuid;
			this.expiresAt = expiresAt;
			this.extra = extra;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Entry && (name.equals(((Entry) obj).name) && uuid.equals(
					((Entry) obj).uuid) && expiresAt.equals(((Entry) obj).expiresAt))
					&& extra.equals(((Entry) obj).extra);
		}

		@Override
		public int hashCode() {
			return name.hashCode() + uuid.hashCode() + expiresAt.hashCode() + extra.hashCode();
		}

		public boolean isExpired() {
			return expiresAt.isBefore(Instant.now());
		}

		@Override
		public String toString() {
			JsonObject object = new JsonObject();
			object.addProperty("name", name);
			object.addProperty("uuid", uuid.toString());
			object.addProperty("expiresAtEpochSecond", expiresAt.getEpochSecond());
			object.addProperty("expiresAtNano", expiresAt.getNano());
			object.add("extra", extra);
			return gson.get().toJson(object);
		}
	}

	private Entry deserialize(String serialized) {
		JsonObject object = gson.get().fromJson(serialized, JsonObject.class);
		String name = object.get("name").getAsJsonPrimitive().getAsString();
		UUID uuid = UUID.fromString(object.get("uuid").getAsJsonPrimitive().getAsString());
		Instant expiresAt = Instant.ofEpochSecond(
				object.get("expiresAtEpochSecond").getAsJsonPrimitive().getAsLong(),
				object.get("expiresAtNano").getAsJsonPrimitive().getAsInt());
		JsonObject extra = object.get("extra").getAsJsonObject();
		return new Entry(name, uuid, expiresAt, extra);
	}

	public static void load() {
		cache = new UserCache(
				PServerPlugin.getInstance().getWorkingDirectory().resolve("usercache"),
				PServerPlugin.getInstance().getWorkingDirectory().resolve("usercache_expired"));
	}

	public static void unload() {
		cache.doUnload();
		cache.byName.clear();
		cache.byUUID.clear();
	}

	public static UserCache cache() {
		return cache;
	}
}
