/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.userapi.data.UserModifier;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class UserAPI {
	private static UserAPI instance;

	UserAPI() {
		if (instance != null)
			throw new IllegalStateException("UserAPI already initialized!");
		instance = this;
	}

	public static UserAPI getInstance() {
		return instance;
	}

	public abstract User getUser(UUID uuid);

	public abstract User getUser(Player player);

	public abstract void loadedUsersForEach(Consumer<? super User> consumer);

	public abstract void addModifier(UserModifier modifier);

	public abstract void removeModifier(UserModifier modifier);

	public abstract void unloadUser(User user);

	public abstract boolean isUserLoaded(UUID uuid);

	public abstract User getIfLoaded(UUID uuid);
}
