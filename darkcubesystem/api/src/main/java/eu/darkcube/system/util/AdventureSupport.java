/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.util;

import eu.darkcube.system.DarkCubeSystem;
import eu.darkcube.system.libs.net.kyori.adventure.platform.bukkit.BukkitAudiences;

public class AdventureSupport {

	private static BukkitAudiences audienceProvider;

	public static BukkitAudiences audienceProvider() {
		if (audienceProvider == null) {
			audienceProvider = BukkitAudiences.create(DarkCubeSystem.getInstance());
		}
		return audienceProvider;
	}
}
