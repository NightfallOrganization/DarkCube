/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.bukkit;

import eu.darkcube.system.pserver.common.UniqueId;

public class WrapperUniqueIdProvider extends UniqueIdProvider {

	static {
		new WrapperUniqueIdProvider();
	}

	public static void init() {

	}

	@Override
	public boolean isAvailable(UniqueId id) {
		return false;
	}

	@Override
	public UniqueId newUniqueId() {
		return new PacketWrapperNodeNewUniqueId().sendQuery().cast(PacketNodeWrapperUniqueId.class)
				.getUniqueId();
	}
}
