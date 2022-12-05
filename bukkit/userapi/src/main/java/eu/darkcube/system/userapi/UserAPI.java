package eu.darkcube.system.userapi;

import java.math.BigInteger;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import de.dytanic.cloudnet.common.concurrent.ITask;
import de.dytanic.cloudnet.common.concurrent.ITaskListener;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.packetapi.PacketAPI;
import eu.darkcube.system.userapi.data.UserModifier;
import eu.darkcube.system.userapi.events.UserLoadEvent;
import eu.darkcube.system.userapi.events.UserUnloadEvent;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataRemove;
import eu.darkcube.system.userapi.packets.PacketUserPersistentDataSet;

public class UserAPI {

	private static UserAPI instance;
	private static final long UNLOAD_USERS_AFTER_MILLIS = TimeUnit.SECONDS.toMillis(20);
	private final Database database;
	private final Map<UUID, BukkitUser> users = new HashMap<>();
	private final UListener listener = new UListener();
	private final BukkitRunnable unloader;
	private final Deque<UserModifier> modifiers = new ConcurrentLinkedDeque<>();

	private UserAPI() {
		instance = this;
		database = CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("userapi_users");
		Bukkit.getPluginManager().registerEvents(listener, PluginUserAPI.getInstance());

		// Migration process
		PluginUserAPI.getInstance().getLogger().info("Starting migration process");
		if (CloudNetDriver.getInstance().getDatabaseProvider().containsDatabase("user_languages")) {
			Database db = CloudNetDriver.getInstance().getDatabaseProvider()
					.getDatabase("user_languages");
			for (String key : db.keys()) {
				UUID uuid = UUID.fromString(key);
				User user = getUser(uuid);
				user.setLanguage(db.get(key).get("language", Language.class));
				PluginUserAPI.getInstance().getLogger().info(
						"Migrating language: " + user.getName() + "(" + user.getUniqueId() + ")");
				unloadUser(user, true);
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
				PluginUserAPI.getInstance().getLogger().info(
						"Migrating cubes: " + user.getName() + "(" + user.getUniqueId() + ")");
				unloadUser(user, true);
			}
			CloudNetDriver.getInstance().getDatabaseProvider().deleteDatabase("cubesapi_cubes");
		}
		PacketAPI.getInstance().registerHandler(PacketUserPersistentDataSet.class, packet -> {
			if (isUserLoaded(packet.getUniqueId())) {
				User user = getUser(packet.getUniqueId());
				if (user instanceof BukkitUser) {
					BukkitUser bu = (BukkitUser) user;
					synchronized (UserAPI.this) {
						bu.getPersistentDataStorage().getData().append(packet.getData());
					}
				}
			}
			return null;
		});
		PacketAPI.getInstance().registerHandler(PacketUserPersistentDataRemove.class, packet -> {
			if (isUserLoaded(packet.getUniqueId())) {
				User user = getUser(packet.getUniqueId());
				if (user instanceof BukkitUser) {
					BukkitUser bu = (BukkitUser) user;
					synchronized (UserAPI.this) {
						bu.getPersistentDataStorage().getData().remove(packet.getKey().toString());
					}
				}
			}
			return null;
		});
		unloader = new BukkitRunnable() {
			@Override
			public void run() {
				for (BukkitUser user : users.values()) {
					if (user.lastAccess() + UNLOAD_USERS_AFTER_MILLIS < System
							.currentTimeMillis()) {
						unloadUser(user);
					}
				}
			}
		};
		unloader.runTaskTimerAsynchronously(PluginUserAPI.getInstance(), 1,
				TimeUnit.HOURS.toSeconds(1) * 20);
	}

	public synchronized User getUser(UUID uuid) {
		if (!users.containsKey(uuid)) {
			return loadUser(uuid);
		}
		return users.get(uuid);
	}

	synchronized User existingUser(UUID uuid) {
		return users.get(uuid);
	}

	public synchronized void addModifier(UserModifier modifier) {
		modifiers.add(modifier);
		for (User user : users.values()) {
			modifier.onLoad(user);
		}
	}

	public synchronized void removeModifier(UserModifier modifier) {
		modifiers.remove(modifier);
		for (User user : users.values()) {
			modifier.onUnload(user);
		}
	}

	public synchronized boolean isUserLoaded(User user) {
		return user.isLoaded();
	}

	public synchronized User getUser(Player player) {
		if (!users.containsKey(player.getUniqueId())) {
			return loadUser(player);
		}
		return users.get(player.getUniqueId());
	}

	public synchronized boolean isUserLoaded(UUID uuid) {
		return users.containsKey(uuid);
	}

	private synchronized User loadUser(UUID uuid) {
		if (users.containsKey(uuid)) {
			return users.get(uuid);
		}
		long time1 = System.currentTimeMillis();
		String name = uuid.toString().substring(0, 16);
		JsonDocument persistentData = new JsonDocument();
		if (database.contains(uuid.toString())) {
			JsonDocument doc = database.get(uuid.toString());
			uuid = UUID.fromString(doc.getString("uuid"));
			name = doc.getString("name");
			persistentData = doc.getDocument("persistentData");
		}
		long time2 = System.currentTimeMillis();
		BukkitUser user = new BukkitUser(this, uuid, name);
		user.getPersistentDataStorage().getData().append(persistentData);
		user.getCubes();
		long time3 = System.currentTimeMillis();
		user.getLanguage();
		loadUser(user);
		if (System.currentTimeMillis() - time1 > 1000) {
			PluginUserAPI.getInstance().getLogger()
					.info("Loading user took very long: " + (System.currentTimeMillis() - time1)
							+ " | " + (System.currentTimeMillis() - time2) + " | "
							+ (System.currentTimeMillis() - time3));
		}
		return user;
	}

	synchronized void loadUser(BukkitUser user) {
		users.put(user.getUniqueId(), user);
		modifiers.forEach(m -> m.onLoad(user));
		CloudNetDriver.getInstance().getEventManager().callEvent(new UserLoadEvent(user));
		user.loading = false;
		user.loaded(true);
	}

	private synchronized User loadUser(Player player) {
		User user = loadUser(player.getUniqueId());
		if (user instanceof BukkitUser) {
			((BukkitUser) user).player(player);
		}
		return user;
	}

	public synchronized void unloadUser(User user) {
		unloadUser(user, false);
	}

	public synchronized void unloadUser(User user, boolean sync) {
		BukkitUser u;
		u = users.get(user.getUniqueId());
		if (u == null) {
			throw new IllegalArgumentException("User " + user.getName() + " is not loaded!");
		}
		CloudNetDriver.getInstance().getEventManager().callEvent(new UserUnloadEvent(u));
		saveUser(u, sync);
		modifiers.forEach(m -> m.onUnload(u));
		u.loaded(false);
		users.remove(user.getUniqueId());
	}

	public synchronized void saveUser(User user) {
		saveUser(user, false);
	}

	public synchronized void saveUser(User user, boolean sync) {
		JsonDocument doc = new JsonDocument();
		doc.append("uuid", user.getUniqueId());
		doc.append("name", user.getName());
		JsonDocument persistentData = new JsonDocument();
		BukkitUser bu = (BukkitUser) user;
		// for (Entry<Key, byte[]> e : ((BasicPersistentDataStorage)
		// user.getPersistentDataStorage())
		// .getData().entrySet()) {
		// persistentData.append(e.getKey().toString(), e.getValue());
		// }
		persistentData.append(bu.getPersistentDataStorage().getData());
		doc.append("persistentData", persistentData);
		ITaskListener<Boolean> failureListener = new ITaskListener<Boolean>() {
			@Override
			public void onCancelled(ITask<Boolean> task) {
				new IllegalStateException("CANT CANCEL THIS TASK!!! THINGS MAY BREAK!!!")
						.printStackTrace();
			}

			@Override
			public void onFailure(ITask<Boolean> task, Throwable th) {
				new IllegalStateException("TASK FAILED!!! THINGS MAY BREAK!!!", th)
						.printStackTrace();
			}
		};
		if (!sync) {
			database.containsAsync(user.getUniqueId().toString()).fireExceptionOnFailure()
					.addListener(new ITaskListener<Boolean>() {
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
							new IllegalStateException(
									"CANT CANCEL THIS TASK!!! THINGS MAY BREAK!!!")
											.printStackTrace();
						}

						@Override
						public void onFailure(ITask<Boolean> task, Throwable th) {
							new IllegalStateException("TASK FAILED!!! THINGS MAY BREAK!!!", th)
									.printStackTrace();
						}
					});
		} else {
			if (database.contains(user.getUniqueId().toString())) {
				database.update(user.getUniqueId().toString(), doc);
			} else {
				database.insert(user.getUniqueId().toString(), doc);
			}
		}
	}

	public class UListener implements Listener {
		@EventHandler(priority = EventPriority.LOWEST)
		public void handle(PlayerJoinEvent event) {
			loadUser(event.getPlayer());
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void handle(PlayerQuitEvent event) {
			unloadUser(getUser(event.getPlayer()));
		}
	}

	public static UserAPI getInstance() {
		return instance;
	}

	static {
		new UserAPI();
	}
}
