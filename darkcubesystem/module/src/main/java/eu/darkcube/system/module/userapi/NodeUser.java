/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.userapi;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.userapi.CommonUser;
import eu.darkcube.system.userapi.CommonUserData;

public class NodeUser extends CommonUser {

    public NodeUser(CommonUserData userData) {
        super(userData);
    }

    @Override public @NotNull Audience audience() {
        return Audience.empty();
    }
}
