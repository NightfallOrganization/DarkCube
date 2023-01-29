/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public class ViaSupport {

	private static boolean supported;

	static {
		try {
			Via.getAPI();
			supported = true;
		} catch (Throwable t) {
			supported = false;
		}
	}

	public static int getVersion(Player player) {
		if (supported) {
			return Via.getAPI().getPlayerVersion(player.getUniqueId());
		}
		return Integer.MAX_VALUE;
	}
}
