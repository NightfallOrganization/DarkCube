/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.events.UserLoadEvent;
import eu.darkcube.system.userapi.events.UserUnloadEvent;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataSet;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

public class BukkitUserAPI extends AbstractUserAPI {
	private static final long UNLOAD_USERS_AFTER_MILLIS = TimeUnit.SECONDS.toMillis(20);
	private static BukkitUserAPI instance;

	static {
		new BukkitUserAPI();
	}

	final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(false);
	final HashMap<UUID, BukkitUser> users = new HashMap<>();
	final ConcurrentHashMap<UUID, BukkitUser> saving = new ConcurrentHashMap<>();
	private final Database database;

	private BukkitUserAPI() {
		super();
		instance = this;
		database = CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("userapi_users");
		UListener listener = new UListener();
		Bukkit.getPluginManager().registerEvents(listener, DarkCubeSystem.getInstance());

		// Migration process
		DarkCubeSystem.getInstance().getLogger().info("Starting migration process");
		if (CloudNetDriver.getInstance().getDatabaseProvider().containsDatabase("user_languages")) {
			Database db = CloudNetDriver.getInstance().getDatabaseProvider()
					.getDatabase("user_languages");
			for (String key : db.keys()) {
				UUID uuid = UUID.fromString(key);
				User user = getUser(uuid);
				user.setLanguage(db.get(key).get("language", Language.class));
				DarkCubeSystem.getInstance().getLogger()
						.info("Migrating language: " + user.getName() + "(" + user.getUniqueId()
								+ ")");
				unloadUser(user);
			}
			CloudNetDriver.getInstance().getDatabaseProvider().deleteDatabase("user_languages");
		}
		if (CloudNetDriver.getInstance().getDatabaseProvider().containsDatabase("cubesapi_cubes")) {
			Database db = CloudNetDriver.getInstance().getDatabaseProvider()
					.getDatabase("cubesapi_cubes");
			for (String key : db.keys()) {
				JsonDocument doc = db.get(key);
				BigInteger cubes = doc.getBigInteger("cubes");
				User user = getUser(UUID.fromString(key));
				user.setCubes(cubes);
				DarkCubeSystem.getInstance().getLogger()
						.info("Migrating cubes: " + user.getName() + "(" + user.getUniqueId()
								+ ")");
				unloadUser(user);
			}
			CloudNetDriver.getInstance().getDatabaseProvider().deleteDatabase("cubesapi_cubes");
		}
		PacketAPI.getInstance().registerHandler(PacketUserPersistentDataSet.class, packet -> {
			if (isUserLoaded(packet.getUniqueId())) {
				AsyncWrapperUser user = getUser(packet.getUniqueId());
				try {
					user.lock();
					user.getPersistentDataStorage().getData().append(packet.getData());
				} finally {
					user.unlock();
				}
			}
			return null;
		});
		PacketAPI.getInstance().registerHandler(PacketUserPersistentDataRemove.class, packet -> {
			if (isUserLoaded(packet.getUniqueId())) {
				AsyncWrapperUser user = getUser(packet.getUniqueId());
				try {
					user.lock();
					user.getPersistentDataStorage().getData().remove(packet.getKey().toString());
				} finally {
					user.unlock();
				}
			}
			return null;
		});
		new BukkitRunnable() {
			@Override
			public void run() {
				List<User> unload = new ArrayList<>();
				lock.readLock().lock();
				for (BukkitUser user : users.values()) {
					Player player = user.asPlayer();
					if (player != null && player.isOnline()) {
						user.lastAccess(System.currentTimeMillis());
						continue;
					}
					if (user.lastAccess() + UNLOAD_USERS_AFTER_MILLIS - System.currentTimeMillis()
							< 0) {
						if (user.lock.tryLock()) {
							user.lock.unlock();
							unload.add(user);
						}
					}
				}
				lock.readLock().unlock();
				if (!unload.isEmpty()) {
					lock.writeLock().lock();
					unload.forEach(u -> unloadUser(u));
					lock.writeLock().unlock();
				}
			}
		}.runTaskTimerAsynchronously(DarkCubeSystem.getInstance(), 1,
				TimeUnit.HOURS.toSeconds(1) * 20);
	}

	public static BukkitUserAPI getInstance() {
		return instance;
	}

	public static void init() {
	}

	public AsyncWrapperUser getUser(UUID uuid) {
		lock.readLock().lock();
		BukkitUser bu = users.get(uuid);
		if (bu == null) {
			lock.readLock().unlock();
			lock.writeLock().lock();
			lock.readLock().lock();
			bu = users.get(uuid);
			if (bu == null) {
				bu = loadUser(uuid);
			}
			lock.writeLock().unlock();
		}
		lock.readLock().unlock();
		return new AsyncWrapperUser(bu);
	}

	@Override
	public boolean isUserLoaded(UUID uuid) {
		try {
			lock.readLock().lock();
			return users.containsKey(uuid);
		} finally {
			lock.readLock().unlock();
		}
	}

	public AsyncWrapperUser getIfLoaded(UUID uuid) {
		lock.readLock().lock();
		BukkitUser bukkitUser = users.get(uuid);
		lock.readLock().unlock();
		return bukkitUser == null ? null : new AsyncWrapperUser(bukkitUser);
	}

	BukkitUser loadUser(UUID uuid) {
		lock.writeLock().lock();
		BukkitUser usaving = saving.get(uuid);
		long time1 = System.currentTimeMillis();
		String name = uuid.toString().substring(0, 16);
		JsonDocument persistentData = new JsonDocument();
		BukkitUser user;
		if (usaving == null) {
			if (users.containsKey(uuid)) {
				return users.get(uuid);
			}
			if (database.contains(uuid.toString())) {
				JsonDocument doc = database.get(uuid.toString());
				uuid = UUID.fromString(doc.getString("uuid"));
				name = doc.getString("name");
				persistentData = doc.getDocument("persistentData");
			}
			user = new BukkitUser(uuid, name);
		} else {
			user = usaving;
		}
		long time2 = System.currentTimeMillis();
		user.getPersistentDataStorage().getData().append(persistentData);
		user.getCubes();
		long time3 = System.currentTimeMillis();
		user.getLanguage();
		users.put(user.getUniqueId(), user);
		modifiers.forEach(m -> m.onLoad(user));
		CloudNetDriver.getInstance().getEventManager().callEvent(new UserLoadEvent(user));
		user.loaded(true);
		if (System.currentTimeMillis() - time1 > 1000) {
			DarkCubeSystem.getInstance().getLogger()
					.info("Loading user took very long: " + (System.currentTimeMillis() - time1)
							+ " | " + (System.currentTimeMillis() - time2) + " | " + (
							System.currentTimeMillis() - time3));
		}
		lock.writeLock().unlock();
		return user;
	}

	@Override
	public void unloadUser(User user) {
		lock.writeLock().lock();
		BukkitUser u = users.get(user.getUniqueId());
		if (u == null) {
			lock.writeLock().unlock();
			throw new IllegalArgumentException("User " + user.getName() + " is not loaded!");
		}
		CloudNetDriver.getInstance().getEventManager().callEvent(new UserUnloadEvent(u));
		JsonDocument doc = new JsonDocument();
		doc.append("uuid", user.getUniqueId());
		doc.append("name", user.getName());
		JsonDocument persistentData = new JsonDocument();
		AsyncWrapperUser bu = (AsyncWrapperUser) user;
		persistentData.append(bu.getPersistentDataStorage().getData());
		doc.append("persistentData", persistentData);
		saving.put(user.getUniqueId(), u);
		database.containsAsync(user.getUniqueId().toString()).fireExceptionOnFailure()
				.addListener(new ITaskListener<Boolean>() {
					ITaskListener<Boolean> failureListener = new ITaskListener<Boolean>() {
						@Override
						public void onCancelled(ITask<Boolean> task) {
							saving.remove(user.getUniqueId());
							new IllegalStateException(
									"DON'T CANCEL THIS TASK!!! THINGS MAY BREAK!!!").printStackTrace();
						}

						@Override
						public void onFailure(ITask<Boolean> task, Throwable th) {
							saving.remove(user.getUniqueId());
							new IllegalStateException("TASK FAILED!!! THINGS MAY BREAK!!!",
									th).printStackTrace();
						}

						@Override
						public void onComplete(ITask<Boolean> task, Boolean aBoolean) {
							saving.remove(user.getUniqueId());
						}
					};

					@Override
					public void onComplete(ITask<Boolean> task, Boolean t) {
						if (t) {
							database.updateAsync(user.getUniqueId().toString(), doc)
									.fireExceptionOnFailure().addListener(failureListener);
						} else {
							database.insertAsync(user.getUniqueId().toString(), doc)
									.fireExceptionOnFailure().addListener(failureListener);
						}
					}

					@Override
					public void onCancelled(ITask<Boolean> task) {
						saving.remove(user.getUniqueId());
						new IllegalStateException(
								"CANT CANCEL THIS TASK!!! THINGS MAY BREAK!!!").printStackTrace();
					}

					@Override
					public void onFailure(ITask<Boolean> task, Throwable th) {
						saving.remove(user.getUniqueId());
						new IllegalStateException("TASK FAILED!!! THINGS MAY BREAK!!!",
								th).printStackTrace();
					}
				});
		modifiers.forEach(m -> m.onUnload(u));
		u.loaded(false);
		users.remove(user.getUniqueId());
		lock.writeLock().unlock();
	}

	@Override
	public void loadedUsersForEach(Consumer<? super User> consumer) {
		lock.readLock().lock();
		users.values().forEach(consumer);
		lock.readLock().unlock();
	}


	public class UListener implements Listener {
		@EventHandler(priority = EventPriority.LOWEST)
		public void handle(PlayerJoinEvent event) {
			loadUser(event.getPlayer().getUniqueId());
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void handle(PlayerQuitEvent event) {
			unloadUser(getUser(event.getPlayer()));
		}
	}
}
