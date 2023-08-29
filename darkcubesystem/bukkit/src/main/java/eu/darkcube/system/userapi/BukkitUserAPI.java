/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import java.util.UUID;

public class BukkitUserAPI extends CommonUserAPI {

    public BukkitUserAPI() {
    }

    @Override protected CommonUser loadUser(UUID uniqueId) {
        return new BukkitUser(uniqueId);
    }
}
