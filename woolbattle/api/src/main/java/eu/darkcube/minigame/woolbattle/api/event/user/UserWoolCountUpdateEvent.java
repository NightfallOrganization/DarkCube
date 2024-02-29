/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.user;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;

public class UserWoolCountUpdateEvent extends UserEvent.Event {
    private final int count;

    public UserWoolCountUpdateEvent(WBUser user, int count) {
        super(user);
        this.count = count;
    }

    public int count() {
        return count;
    }
}
