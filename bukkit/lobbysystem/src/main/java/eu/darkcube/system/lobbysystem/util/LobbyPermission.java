/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.util;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class LobbyPermission extends Permission {

	public static final Permission REWARDS_1 = new LobbyPermission("lobbysystem.dailyrewards.1");
	public static final Permission REWARDS_2 = new LobbyPermission("lobbysystem.dailyrewards.2");
	public static final Permission REWARDS_3 = new LobbyPermission("lobbysystem.dailyrewards.3");
	public static final Permission REWARDS_4 = new LobbyPermission("lobbysystem.dailyrewards.4");
	public static final Permission REWARDS_5 = new LobbyPermission("lobbysystem.dailyrewards.5");
	public static final Permission REWARDS_6 = new LobbyPermission("lobbysystem.dailyrewards.6");
	public static final Permission REWARDS_7 = new LobbyPermission("lobbysystem.dailyrewards.7");
	public static final Permission REWARDS_8 = new LobbyPermission("lobbysystem.dailyrewards.8");
	public static final Permission REWARDS_9 = new LobbyPermission("lobbysystem.dailyrewards.9");
	
	static {
		REWARDS_9.addParent(REWARDS_8, true);
		REWARDS_8.addParent(REWARDS_7, true);
		REWARDS_7.addParent(REWARDS_6, true);
		REWARDS_6.addParent(REWARDS_5, true);
		REWARDS_5.addParent(REWARDS_4, true);
		REWARDS_4.addParent(REWARDS_3, true);
		REWARDS_3.addParent(REWARDS_2, true);
		REWARDS_2.addParent(REWARDS_1, true);
	}
	
	public static void registerAll() {
		register(REWARDS_1);
		register(REWARDS_2);
		register(REWARDS_3);
		register(REWARDS_4);
		register(REWARDS_5);
		register(REWARDS_6);
		register(REWARDS_7);
		register(REWARDS_8);
		register(REWARDS_9);
	}
	
	private static void register(Permission perm) {
		if(Bukkit.getPluginManager().getPermission(perm.getName()) == null)
			Bukkit.getPluginManager().addPermission(perm);
	}
	
	public LobbyPermission(String name, Map<String, Boolean> children) {
		super(name, children);
	}

	public LobbyPermission(String name, PermissionDefault defaultValue, Map<String, Boolean> children) {
		super(name, defaultValue, children);
	}

	public LobbyPermission(String name, PermissionDefault defaultValue) {
		super(name, defaultValue);
	}

	public LobbyPermission(String name, String description, Map<String, Boolean> children) {
		super(name, description, children);
	}

	public LobbyPermission(String name, String description, PermissionDefault defaultValue,
			Map<String, Boolean> children) {
		super(name, description, defaultValue, children);
	}

	public LobbyPermission(String name, String description, PermissionDefault defaultValue) {
		super(name, description, defaultValue);
	}

	public LobbyPermission(String name, String description) {
		super(name, description);
	}

	public LobbyPermission(String name) {
		super(name);
	}
}
