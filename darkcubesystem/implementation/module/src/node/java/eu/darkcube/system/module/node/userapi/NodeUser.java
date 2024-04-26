/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.node.userapi;

import eu.darkcube.system.impl.common.userapi.CommonUser;
import eu.darkcube.system.impl.common.userapi.CommonUserData;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class NodeUser extends CommonUser {

    public NodeUser(CommonUserData userData) {
        super(userData);
    }

    @Override
    public @NotNull Audience audience() {
        return Audience.empty();
    }
}
