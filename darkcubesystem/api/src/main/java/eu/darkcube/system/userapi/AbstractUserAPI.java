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
import java.util.concurrent.ConcurrentLinkedDeque;

public abstract class AbstractUserAPI extends UserAPI {
	protected final ConcurrentLinkedDeque<UserModifier> modifiers = new ConcurrentLinkedDeque<>();

	@Override
	public final User getUser(Player player) {
		return getUser(player.getUniqueId());
	}

	@Override
	public final void addModifier(UserModifier modifier) {
		modifiers.add(modifier);
		loadedUsersForEach(modifier::onLoad);
	}

	@Override
	public final void removeModifier(UserModifier modifier) {
		loadedUsersForEach(modifier::onUnload);
		modifiers.remove(modifier);
	}

	@Override
	public boolean isUserLoaded(UUID uuid) {
		return getIfLoaded(uuid) != null;
	}
}
