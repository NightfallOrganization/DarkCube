/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.userapi;

import eu.darkcube.system.libs.net.kyori.adventure.audience.Audience;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.AdventureSupport;

import java.util.UUID;

public class BukkitUser extends CommonUser {
    public BukkitUser(UUID uniqueId) {
        super(uniqueId);
    }

    @Override public @NotNull Audience audience() {
        return AdventureSupport.audienceProvider().player(uniqueId());
    }
}
