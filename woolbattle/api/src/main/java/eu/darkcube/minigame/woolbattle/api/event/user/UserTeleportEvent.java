/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class UserTeleportEvent extends UserEvent.Cancellable {
    private final @NotNull Location location;

    public UserTeleportEvent(@NotNull WBUser user, @NotNull Location location) {
        super(user);
        this.location = location;
    }

    public @NotNull Location location() {
        return location;
    }
}
