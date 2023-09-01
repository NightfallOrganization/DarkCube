/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.module.wrapper.userapi;

import eu.darkcube.system.common.userapi.CommonUser;
import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AdventureSupport;

import java.util.UUID;

public class WrapperUser extends CommonUser {
    public WrapperUser(UUID uniqueId) {
        super(uniqueId);
    }

    @Override public @NotNull Audience audience() {
        return AdventureSupport.adventureSupport().audienceProvider().player(uniqueId());
    }
}
