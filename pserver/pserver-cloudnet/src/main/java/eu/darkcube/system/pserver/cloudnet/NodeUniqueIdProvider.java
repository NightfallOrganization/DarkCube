/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.pserver.cloudnet;

import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;

import java.util.UUID;

public class NodeUniqueIdProvider extends UniqueIdProvider {

	@Override
	public UniqueId newUniqueId() {
		UniqueId id;
		do {
			UUID uuid = UUID.randomUUID();
			id = new UniqueId(uuid.toString());
		} while (!isAvailable(id));
		return id;
	}

	@Override
	public boolean isAvailable(UniqueId id) {
		return !DatabaseProvider.get("pserver").cast(PServerDatabase.class).contains(id)
				&& !NodePServerProvider.getInstance().pservers.containsKey(id);
	}

	public static void init() {

	}

	static {
		new NodeUniqueIdProvider();
	}
}
