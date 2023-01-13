/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.version;

import eu.darkcube.system.util.ReflectionUtils;
import org.bukkit.Bukkit;

public class VersionSupport {

	private static Version version;

	public static Version getVersion() {
		return version;
	}

	public static void init() {
		Class<? extends Version> cls = ReflectionUtils.getClass(
						VersionSupport.class.getPackage().getName() + ".Version_" + Bukkit.getServer()
								.getBukkitVersion().replace('.', '_').split("-", 2)[0])
				.asSubclass(Version.class);
		version = ReflectionUtils.instantiateObject(cls);
		version.init();
	}

}
