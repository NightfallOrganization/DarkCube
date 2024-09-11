/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.perk;

import eu.darkcube.minigame.woolbattle.api.entity.Arrow;
import eu.darkcube.minigame.woolbattle.api.event.user.UserEvent;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class BowShootArrowEvent extends UserEvent.Event {
    private final @NotNull Arrow arrow;

    public BowShootArrowEvent(@NotNull WBUser user, @NotNull Arrow arrow) {
        super(user);
        this.arrow = arrow;
    }

    public @NotNull Arrow arrow() {
        return arrow;
    }
}
