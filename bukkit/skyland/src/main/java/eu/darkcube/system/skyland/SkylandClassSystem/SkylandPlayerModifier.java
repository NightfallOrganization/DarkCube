/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.data.UserModifier;
import eu.darkcube.system.util.data.Key;

public class SkylandPlayerModifier implements UserModifier {
	private static final Key SKYLAND_PLAYER = new Key(Skyland.getInstance(), "user");

	@Override
	public void onLoad(User user) {
		user.getMetaDataStorage().set(SKYLAND_PLAYER, new SkylandPlayer(user));
	}

	@Override
	public void onUnload(User user) {
		user.getMetaDataStorage().remove(SKYLAND_PLAYER);
	}
}
