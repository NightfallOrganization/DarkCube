/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.smash.api;

import java.util.UUID;

import org.bukkit.OfflinePlayer;

import eu.darkcube.minigame.smash.api.user.User;
import eu.darkcube.minigame.smash.user.UserWrapper;

public class SmashAPI {

	private static final SmashAPI instance = new SmashAPI();
	
	private SmashAPI() {
	}
	
	public User getUser(OfflinePlayer player) {
		return UserWrapper.getUser(player);
	}
	
	public User getUser(UUID uuid) {
		return UserWrapper.getUser(uuid);
	}
	
	public static SmashAPI getApi() {
		return instance;
	}
}
