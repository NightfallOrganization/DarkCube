/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import java.util.Collection;
import java.util.UUID;

public interface UserWrapper {

	User load(UUID uuid);
	User getUser(UUID uuid);
	boolean isLoaded(UUID uuid);
	boolean unload(User user);
	Collection<? extends User> getUsers();
	
}
