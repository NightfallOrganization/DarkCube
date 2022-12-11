/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.user;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.minigame.smash.Main;

public class UserWrapper implements Listener {

	@SuppressWarnings("unused")
	private static UserWrapper instance = new UserWrapper();
	private static Map<UUID, User> users = new HashMap<>();

	public static void init() {
		// Auto static stuff
	}

	public static User getUser(UUID uniqueId) {
		if (users.containsKey(uniqueId)) {
			return users.get(uniqueId);
		}
		return load(uniqueId);
	}

	public static User getUser(OfflinePlayer p) {
		return getUser(p.getUniqueId());
	}

	private UserWrapper() {
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void handle(PlayerQuitEvent e) {
		unload(getUser(e.getPlayer()));
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void handle(PlayerJoinEvent e) {
		getUser(e.getPlayer());
	}

	public static void unload(User user) {
		users.remove(user.getUniqueId()).unregister();
	}

	private static User load(UUID uuid) {
		Language l = Language.GERMAN;
		try {
			Database s = CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("lobbysystem_userdata");
			if (s.get(uuid.toString()) != null) {
				l = Language.fromString(s.get(uuid.toString()).getString("language"));
			}
		} catch (Throwable ex) {
		}
		User user = new User(uuid, l);
		users.put(uuid, user);
		return user;
	}
}
