/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.event.user;

import eu.darkcube.minigame.woolbattle.user.WBUser;
import org.bukkit.event.Event;

public abstract class UserEvent extends Event {
    private final WBUser user;

    public UserEvent(WBUser user) {
        this(false, user);
    }

    public UserEvent(boolean isAsync, WBUser user) {
        super(isAsync);
        this.user = user;
    }

    public WBUser user() {
        return user;
    }
}
