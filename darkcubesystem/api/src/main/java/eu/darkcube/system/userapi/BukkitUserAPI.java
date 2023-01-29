/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.userapi;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.packets.PacketQueryUser;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataUpdate;
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
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class BukkitUserAPI extends AbstractUserAPI {
	private static final long UNLOAD_USERS_AFTER_MILLIS = TimeUnit.SECONDS.toMillis(20);
	private static BukkitUserAPI instance;

	static {
		new BukkitUserAPI();
	}

	final ConcurrentHashMap<UUID, BukkitUser> users = new ConcurrentHashMap<>();

	private BukkitUserAPI() {
		super();
		instance = this;
		UListener listener = new UListener();
		Bukkit.getPluginManager().registerEvents(listener, DarkCubePlugin.systemPlugin());

		// Migration process
		DarkCubePlugin.systemPlugin().getLogger().info("Starting migration process");
		if (CloudNetDriver.getInstance().getDatabaseProvider().containsDatabase("user_languages")) {
			Database db = CloudNetDriver.getInstance().getDatabaseProvider()
					.getDatabase("user_languages");
			for (String key : db.keys()) {
				UUID uuid = UUID.fromString(key);
				User user = getUser(uuid);
				user.setLanguage(db.get(key).get("language", Language.class));
				DarkCubePlugin.systemPlugin().getLogger()
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
				DarkCubePlugin.systemPlugin().getLogger()
						.info("Migrating cubes: " + user.getName() + "(" + user.getUniqueId()
								+ ")");
				unloadUser(user);
			}
			CloudNetDriver.getInstance().getDatabaseProvider().deleteDatabase("cubesapi_cubes");
		}
		PacketAPI.getInstance().registerHandler(PacketUserPersistentDataUpdate.class, packet -> {
			ifLoaded(packet.getUniqueId(),
					user -> user.getPersistentDataStorage().set(packet.getData()));
			return null;
		});
		new BukkitRunnable() {
			@Override
			public void run() {
				List<User> unload = new ArrayList<>();
				for (BukkitUser user : users.values()) {
					Player player = user.asPlayer();
					if (player != null && player.isOnline()) {
						user.lastAccess(System.currentTimeMillis());
						continue;
					}
					if (user.lastAccess() + UNLOAD_USERS_AFTER_MILLIS - System.currentTimeMillis()
							< 0) {
						unload.add(user);
					}
				}
				if (!unload.isEmpty()) {
					unload.forEach(u -> unloadUser(u));
				}
			}
		}.runTaskTimerAsynchronously(DarkCubePlugin.systemPlugin(), 1,
				TimeUnit.MINUTES.toSeconds(1) * 20);
	}

	public static BukkitUserAPI getInstance() {
		return instance;
	}

	public static void init() {
	}

	private void ifLoaded(UUID uuid, Consumer<AsyncWrapperUser> consumer) {
		users.computeIfPresent(uuid, (uid, user) -> {
			consumer.accept(new AsyncWrapperUser(user));
			return user;
		});
	}

	public AsyncWrapperUser getUser(UUID uuid) {
		AtomicBoolean created = new AtomicBoolean(false);
		BukkitUser bu = users.computeIfAbsent(uuid, uid -> {
			BukkitUser b = createUser(uuid);
			created.set(true);
			return b;
		});
		AsyncWrapperUser u = new AsyncWrapperUser(bu);
		if (created.get()) {
			modifiers.forEach(m -> m.onLoad(u));
			bu.loaded(true);
		}
		return u;
	}

	@Override
	public void loadedUsersForEach(Consumer<? super User> consumer) {
		users.values().stream().map(AsyncWrapperUser::new).forEach(consumer);
	}

	@Override
	public void unloadUser(User user) {
		BukkitUser u = users.get(user.getUniqueId());
		if (u == null) {
			throw new IllegalArgumentException("User " + user.getName() + " is not loaded!");
		}
		AsyncWrapperUser au = new AsyncWrapperUser(u);
		modifiers.forEach(m -> m.onUnload(au));
		u.loaded(false);
		users.remove(user.getUniqueId());
	}

	public AsyncWrapperUser getIfLoaded(UUID uuid) {
		BukkitUser bukkitUser = users.get(uuid);
		return bukkitUser == null ? null : new AsyncWrapperUser(bukkitUser);
	}

	@Override
	public boolean isUserLoaded(UUID uuid) {
		return users.containsKey(uuid);
	}

	private BukkitUser createUser(UUID uuid) {
		PacketQueryUser.Result result =
				new PacketQueryUser(uuid).sendQuery().cast(PacketQueryUser.Result.class);
		BukkitUser user = new BukkitUser(uuid, result.getName());
		user.getPersistentDataStorage().set(result.getData());
		return user;
	}

	public class UListener implements Listener {

		@EventHandler(priority = EventPriority.LOWEST)
		public void handle(PlayerJoinEvent event) {
			getUser(event.getPlayer());
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void handle(PlayerQuitEvent event) {
			unloadUser(getUser(event.getPlayer()));
		}
	}
}
