/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.pserver.cloudnet;

import eu.darkcube.system.pserver.cloudnet.database.DatabaseProvider;
import eu.darkcube.system.pserver.cloudnet.database.PServerDatabase;
import eu.darkcube.system.pserver.common.UniqueId;

import java.util.UUID;

public class NodeUniqueIdProvider {

    private static final NodeUniqueIdProvider instance;

    static {
        instance = new NodeUniqueIdProvider();
    }

    public static void init() {

    }

    public static NodeUniqueIdProvider instance() {
        return instance;
    }

    public UniqueId newUniqueId() {
        UniqueId id;
        do {
            UUID uuid = UUID.randomUUID();
            id = new UniqueId(uuid.toString());
        } while (!isAvailable(id));
        return id;
    }

    public boolean isAvailable(UniqueId id) {
        return !DatabaseProvider.get("pserver").cast(PServerDatabase.class).contains(id);
    }
}
