/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.plugin.user;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;

public class UserManager {

	private static UserManager instance;

	private final UserListener listener;
	private final Map<UUID, User> online = new ConcurrentHashMap<>();

	private UserManager() {
		this.listener = new UserListener();
		this.listener.register();
	}

	void playerJoin(Player p) {
		if (!online.containsKey(p.getUniqueId())) {
			online.put(p.getUniqueId(), new OnlineUser(p));
		}
	}

	void playerQuit(Player p) {
		if (online.containsKey(p.getUniqueId())) {
			online.remove(p.getUniqueId()).saveExtra();
		}
	}
	
	public User getUser(Player p) {
		return getUser(p.getUniqueId());
	}

	public User getUser(UUID uuid) {
		return online.getOrDefault(uuid, new OfflineUser(uuid));
	}

	private void close() {
		this.listener.unregister();
	}

	public static void register() {
		instance = new UserManager();
	}

	public static void unregister() {
		instance.close();
		instance = null;
	}

	public static UserManager getInstance() {
		return instance;
	}
}
